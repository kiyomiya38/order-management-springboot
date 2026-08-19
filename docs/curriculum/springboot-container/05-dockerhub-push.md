# 05 Docker Hubへimageをpushする

[前へ：起動後の操作と再ビルド](./04-operation-and-rebuild.md) ｜ [次へ：Kubernetes CI/CDデプロイ課題](./06-kubernetes-cicd-assignment.md)

この章では、前章までに使用したSpring BootアプリのimageをDocker Hubへpushします。

PC上で作ったimageは、そのままではほかのPCから利用できません。Docker Hubへpushすると、インターネット上のimage保管場所であるレジストリから取得できるようになります。

この章は、手順1から手順8までを上から順番に実行するハンズオンです。各手順の「ここまでの確認」で結果を確かめてから、次へ進んでください。

## この章のゴール

- Docker HubにPublicリポジトリを作成できる
- Personal Access Tokenを使ってDocker CLIからログインできる
- DockerfileからSpring Bootアプリのimageを作成できる
- imageへDocker Hub用の名前とバージョンを付けられる
- imageをDocker Hubへpushできる
- Docker Hub画面で`1.0.0`タグを確認できる

## 前提条件

- Docker Hubアカウントを取得済みである
- 書き込み権限を持つPersonal Access Tokenを作成済みである
- Docker Desktopが起動している
- 第3章でDockerfileと`.dockerignore`を作成済みである
- インターネットへ接続できる

## 用語を確認する

| 用語 | この演習での意味 |
| --- | --- |
| レジストリ | imageを保管・共有するサービス。今回はDocker Hub |
| リポジトリ | 1つのアプリに関するimageをまとめる場所 |
| namespace | Docker Hub上のユーザー名または組織名 |
| tag | imageの版を区別する名前。今回は`1.0.0` |
| push | PC上のimageをレジストリへ送信する操作 |

この演習で使用する完全なimage名は次の形です。

```text
YOUR_DOCKERHUB_USERNAME/attendance-management:1.0.0
```

`YOUR_DOCKERHUB_USERNAME`は自分のDocker Hubユーザー名へ置き換えます。

```text
YOUR_DOCKERHUB_USERNAME / attendance-management : 1.0.0
└─ namespace               └─ リポジトリ名          └─ tag
```

## 演習前の注意

- Docker HubのPublicリポジトリへpushしたimageは、誰でも取得できます。
- `.env`やPersonal Access Tokenをimageへ含めたり、Docker Hubへ掲載したりしてはいけません。
- Personal Access Tokenをコマンドへ直接書くと、ターミナル履歴に残る可能性があります。
- この演習ではSpring Bootアプリのimageだけをpushします。MariaDBは公式imageを使用するため、自分のリポジトリへpushしません。
- `latest`ではなく、版が分かる`1.0.0`タグを使用します。

この教材のDockerfileは`pom.xml`と`src`だけをコピーします。また、`.dockerignore`で`.env`を除外しています。そのため、教材どおりのファイルを使えば`.env`はimageへ入りません。

一方、Public image内のJARや設定ファイルは、imageを取得した人が調査できます。このアプリには学習用の初期設定も含まれているため、作成するimageは研修専用です。実際の業務データやパスワードを追加せず、本番環境では使用しません。

## 演習の全体像

この章では、次の順番で操作します。

```text
手順1  作業場所と必要なファイルを確認する
  ↓
手順2  Docker HubにPublicリポジトリを作成する
  ↓
手順3  Docker Hubユーザー名を変数へ設定する
  ↓
手順4  Personal Access Tokenでログインする
  ↓
手順5  Spring Bootアプリのimageをビルドする
  ↓
手順6  Docker Hub用の名前を付ける
  ↓
手順7  Docker Hubへpushする
  ↓
手順8  Docker Hub画面で1.0.0タグを確認する
```

## 手順1：作業場所と必要なファイルを確認する

### 目的

Dockerfileがある正しいフォルダで作業し、image作成に必要なファイルが揃っていることを確認します。

### 操作

Git Bashで次を実行します。

```bash
cd ~/order-management-springboot/stages/springboot-container
pwd
test -f Dockerfile && echo "Dockerfile: OK"
test -f .dockerignore && echo ".dockerignore: OK"
test -f pom.xml && echo "pom.xml: OK"
test -d src && echo "src: OK"
grep -n -F -x ".env" .dockerignore
```

### 期待される結果

現在位置として、次のようなパスが表示されます。

```text
/c/Users/ユーザー名/order-management-springboot/stages/springboot-container
```

続けて4つの`OK`と、`.dockerignore`内の`.env`が表示されます。

### この操作の意味

`docker build`は、実行したフォルダを基準にDockerfileやコピー対象を探します。別のフォルダで実行すると、Dockerfileが見つからない、または必要なソースをコピーできないエラーになります。

`.dockerignore`の`.env`は、パスワードを含む設定ファイルをDockerのbuild対象から除外するための指定です。

### ここまでの確認

- [ ] 作業場所が`stages/springboot-container`である
- [ ] 4つの`OK`が表示された
- [ ] `.dockerignore`に`.env`がある

## 手順2：Docker HubにPublicリポジトリを作成する

### 目的

Spring Bootアプリのimageを保存する場所をDocker Hub上に用意します。

### 操作

1. [Docker Hub](https://hub.docker.com/)へサインインする
2. `My Hub`から`Repositories`を開く
3. `Create repository`を選択する
4. `Namespace`で自分のDocker Hubユーザー名を選択する
5. `Repository Name`へ`attendance-management`と入力する
6. `Visibility`で`Public`を選択する
7. `Create`を選択する

リポジトリ名は作成後に変更できないため、`attendance-management`と入力できていることを確認してから作成します。詳しくは[Docker公式：Create a repository](https://docs.docker.com/docker-hub/repos/create/)を参照してください。

すでに自分のnamespaceに同名のPublicリポジトリがある場合は、新しく作り直さず、そのリポジトリを使用できます。

### 期待される結果

Docker Hubで`attendance-management`リポジトリの画面が開き、VisibilityがPublicとして表示されます。

### この操作の意味

リポジトリは、同じアプリに関する複数のimageをまとめる場所です。今後別の版をpushする場合も、同じリポジトリへ異なるtagを付けて保存できます。

### ここまでの確認

- [ ] namespaceが自分のDocker Hubユーザー名である
- [ ] リポジトリ名が`attendance-management`である
- [ ] VisibilityがPublicである

## 手順3：Docker Hubユーザー名を変数へ設定する

### 目的

後のコマンドでDocker Hubユーザー名を何度も入力せず、入力間違いを防ぎます。

### 操作

`your-docker-id`の部分だけを、自分のDocker Hubユーザー名へ置き換えて実行します。

```bash
export DOCKERHUB_USERNAME="your-docker-id"
echo "$DOCKERHUB_USERNAME"
```

### 期待される結果

自分のDocker Hubユーザー名が表示されます。

### この操作の意味

`export`は、現在のGit Bashで使用する変数を設定するコマンドです。この設定は、現在開いているGit Bashを閉じるまで使用できます。

Docker Hubユーザー名は秘密情報ではありませんが、メールアドレスや画面上の表示名とは異なる場合があります。手順2で選択したnamespaceと同じ値を設定してください。

### ここまでの確認

- [ ] `your-docker-id`を実際のユーザー名へ置き換えた
- [ ] `echo`で正しいDocker Hubユーザー名が表示された

## 手順4：Personal Access Tokenでログインする

### 目的

Docker CLIから、自分のDocker Hubリポジトリへimageをpushできる状態にします。

### 操作

次を実行します。

```bash
docker login --username "$DOCKERHUB_USERNAME"
```

`Password:`と表示されたら、作成済みのPersonal Access Tokenを貼り付けてEnterキーを押します。

> 入力中は画面に文字や`*`が表示されません。表示されなくても貼り付けられているため、そのままEnterキーを押してください。

### 期待される結果

次のように表示されます。

```text
Login Succeeded
```

### この操作の意味

`docker login`は、Docker CLIをDocker Hubへ認証するコマンドです。`--username`を指定することで、ブラウザを使うdevice code flowではなく、ユーザー名とPersonal Access Tokenでログインします。

Personal Access Tokenには、対象リポジトリへpushできる`Write`権限が必要です。この演習に`Delete`権限は必要ありません。Tokenをコマンドへ直接書いたり、教材、ソースコード、`.env`へ保存したりしません。

詳しくは[Docker公式：Personal access tokens](https://docs.docker.com/security/access-tokens/)を参照してください。

### ここまでの確認

- [ ] `--username`に変数を指定してログインした
- [ ] パスワード入力欄へPersonal Access Tokenを入力した
- [ ] `Login Succeeded`が表示された

## 手順5：Spring Bootアプリのimageをビルドする

### 目的

既存のDockerfileを使い、Spring Bootアプリを実行できるローカルimageを作成します。

### 操作

Dockerfileがある現在のフォルダで、次を実行します。

```bash
docker build -t attendance-management:1.0.0 .
```

### 期待される結果

ビルド処理が最後まで進み、エラーなくGit Bashのプロンプトへ戻ります。初回はMavenのライブラリやベースimageをダウンロードするため、時間がかかる場合があります。

### この操作の意味

`docker build`はDockerfileからimageを作成するコマンドです。

- `-t attendance-management:1.0.0`：imageへ名前とtagを付ける
- 末尾の`.`：現在のフォルダにあるDockerfileと必要なファイルを使用する

Dockerfileの第1段階ではMavenが実行可能JARを作り、第2段階ではJARを実行するためのimageを作ります。

このimageに含まれるのはSpring Bootアプリです。MariaDBは`mariadb:11.4`という別の公式imageであり、今回のpush対象ではありません。

### ここまでの確認

- [ ] `docker build`がエラーなく完了した
- [ ] image名として`attendance-management`を指定した
- [ ] tagとして`1.0.0`を指定した

## 手順6：Docker Hub用の名前を付ける

### 目的

ローカルimageへ、Docker Hub上の保存先を表す完全な名前を追加します。

### 操作

次を実行します。

```bash
docker tag attendance-management:1.0.0 "$DOCKERHUB_USERNAME/attendance-management:1.0.0"
docker image ls "$DOCKERHUB_USERNAME/attendance-management"
```

### 期待される結果

`docker tag`自体は、成功しても通常は何も表示しません。続く`docker image ls`で、次の内容を持つimageが表示されます。

```text
REPOSITORY                                      TAG
自分のDocker Hubユーザー名/attendance-management  1.0.0
```

### この操作の意味

`docker tag`は、image本体をもう一度ビルドしたりコピーしたりするコマンドではありません。同じimageへ、Docker Hubのnamespace、リポジトリ名、tagを含む別の名前を追加します。

Docker Hubはimage名の先頭部分を見て、どのユーザーのどのリポジトリへpushするかを判断します。

### ここまでの確認

- [ ] image名の先頭が自分のDocker Hubユーザー名である
- [ ] リポジトリ名が`attendance-management`である
- [ ] tagが`1.0.0`である

## 手順7：Docker Hubへpushする

### 目的

Docker Hub用の名前を付けたimageを、Publicリポジトリへ送信します。

### 操作

次を実行します。

```bash
docker push "$DOCKERHUB_USERNAME/attendance-management:1.0.0"
```

### 期待される結果

imageの各layerについて、`Pushed`または`Layer already exists`と表示されます。最後に、次のようなdigestが表示されます。

```text
1.0.0: digest: sha256:... size: ...
```

digestの値はimageごとに異なるため、教材と同じ値にはなりません。

### この操作の意味

`docker push`は、PC上のimageをレジストリへ送信するコマンドです。変更されていないlayerがすでにDocker Hubにある場合は、同じデータを再送せず`Layer already exists`と表示されることがあります。

Docker Hubへpushするには、ログインに加えて、正しいnamespace、リポジトリ名、tagをimageへ付ける必要があります。詳しくは[Docker公式：Push images to a repository](https://docs.docker.com/docker-hub/repos/manage/hub-images/push/)を参照してください。

### ここまでの確認

- [ ] `docker push`でエラーが表示されなかった
- [ ] 最後に`1.0.0`と`digest: sha256:...`が表示された

## 手順8：Docker Hub画面で`1.0.0`タグを確認する

### 目的

PCからpushしたimageが、Docker Hubへ正しく保存されたことを画面で確認します。

### 操作

1. Docker Hubへサインインする
2. `My Hub`から`Repositories`を開く
3. `attendance-management`を開く
4. `Tags`を開く
5. `1.0.0`が表示されていることを確認する

### 期待される結果

Docker Hub画面で次を確認できます。

- リポジトリがPublicである
- `1.0.0`タグが表示されている
- pushした日時が表示されている

`1.0.0`だけをpushした場合、`latest`タグは自動的には作成されません。この演習では`1.0.0`が表示されていれば正常です。

### この操作の意味

Git Bashの成功表示だけでなく、Docker Hub上の保存結果まで確認することで、build、tag、pushの一連の操作が完了したと判断できます。

### ここまでの確認

- [ ] 自分のnamespaceにあるリポジトリを開いた
- [ ] リポジトリがPublicである
- [ ] `1.0.0`タグを確認した

## build・tag・pushの関係

この演習で行った処理は、次の順番です。

```text
Dockerfile
    |
    | docker build
    v
PC上のimage
attendance-management:1.0.0
    |
    | docker tag
    v
Docker Hub用の名前
ユーザー名/attendance-management:1.0.0
    |
    | docker push
    v
Docker Hub
Publicリポジトリの1.0.0タグ
```

## この章の完了チェック

- [ ] 作業場所と必要なファイルを確認した
- [ ] `.dockerignore`で`.env`が除外されていることを確認した
- [ ] Docker HubにPublicリポジトリを作成した
- [ ] Docker Hubユーザー名を変数へ設定した
- [ ] Personal Access TokenでDocker CLIへログインした
- [ ] `attendance-management:1.0.0`をビルドした
- [ ] Docker Hub用の名前を付けた
- [ ] Docker Hubへのpushが成功した
- [ ] Docker Hub画面で`1.0.0`タグを確認した
- [ ] Personal Access Tokenや`.env`を公開していない

`docker pull`や`docker logout`はこの演習では行いません。Docker Hub画面で`1.0.0`タグを確認した時点で演習完了です。

[前へ：起動後の操作と再ビルド](./04-operation-and-rebuild.md) ｜ [次へ：Kubernetes CI/CDデプロイ課題](./06-kubernetes-cicd-assignment.md)
