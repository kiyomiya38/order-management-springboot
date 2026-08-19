# 08 トラブルシューティング

[前へ：Kubernetesマルチテナント演習](./07-kubernetes-multitenant-handson.md) ｜ [教材の入口へ戻る](./README.md)

エラーが発生したときは、設定を一度に何か所も変更しないことが大切です。次の順番で「どこまで成功しているか」を確認すると、原因を切り分けやすくなります。

この章では、第5章までのDocker、Docker Compose、Docker Hubに関する問題を扱います。第6章または第7章のKubernetes環境で問題が発生した場合は、これまでに使用したKubernetes、GitHub Actions、GitOps、Argo CDの教材と、第7章の「うまくいかないとき」を参照してください。

```text
docker compose up -d --buildが失敗した
|
+-- dockerコマンド自体が動かない
|     └─ Docker Desktop / WSL 2を確認
|
+-- Composeファイルが見つからない
|     └─ 作業フォルダとファイル名を確認
|
+-- .envのエラー
|     └─ .envの存在と6項目を確認
|
+-- 8081番を使用できない
|     └─ 別のアプリが使用していないか確認
|
+-- dbがhealthyにならない
|     └─ dbログとvolumeの設定を確認
|
+-- 画面操作で403になる
|     └─ セッションとCSRFトークンを新しくする
|
+-- appだけ停止する
      └─ appログ、DB接続先、JAR名を確認
```

Docker Hubへのpushで失敗した場合は、次の順番で確認します。

```text
docker pushが失敗した
|
+-- unauthorized / authentication required
|     └─ Docker Hubユーザー名とログイン状態を確認
|
+-- requested access is denied / insufficient_scope
|     └─ namespace、リポジトリ名、Tokenの書き込み権限を確認
|
+-- tag does not exist
      └─ Docker Hub用の名前をimageへ付けたか確認
```

## 1. まず記録する情報

作業場所を変えたりvolumeを削除したりする前に、次を実行します。

```bash
pwd
docker compose ps -a
docker compose logs --tail=100 app
docker compose logs --tail=100 db
```

エラーメッセージでは、最後の1行だけでなく、その少し前に原因が書かれていることがあります。

> `.env`にはパスワードが含まれます。`.env`の内容や、環境変数を展開した`docker compose config`の出力は、チャットや画面共有へ貼らないでください。

## 2. Dockerコマンドが動かない

### `docker: command not found`

Docker Desktopのインストールが完了していないか、インストール前から開いていたGit Bashを使用している可能性があります。

1. Docker Desktopがインストール済みか確認する
2. Git Bashをすべて閉じる
3. Docker Desktopを起動する
4. 新しいGit Bashを開く
5. 次を確認する

```bash
docker --version
docker compose version
```

解決しない場合は、[第0章 Docker Desktopのインストール](./00-docker-desktop-install.md)へ戻ります。

### `Cannot connect to the Docker daemon`またはDocker Serverが表示されない

Dockerコマンドは見つかっていますが、Docker Engineが動いていません。

1. Docker Desktopを起動する
2. 画面が`Engine running`になるまで待つ
3. 次を実行する

```bash
docker version
```

`Client`だけでなく`Server`も表示されれば正常です。

### Docker DesktopやWSL 2が起動しない

Windows TerminalまたはPowerShellを開き、次を確認します。

```powershell
wsl --status
wsl --version
```

WSLの更新を求められた場合は、研修担当者の指示に従って次を実行し、必要ならWindowsを再起動します。

```powershell
wsl --update
```

Docker DesktopのインストールやWSL 2の有効化では、管理者権限が必要になる場合があります。管理者パスワードを求められて自分に権限がない場合は、回避しようとせず研修担当者またはPC管理者へ依頼してください。

## 3. 作業フォルダまたはファイル名が違う

### `no configuration file provided: not found`

`docker-compose.yml`があるフォルダで実行していません。

```bash
cd ~/order-management-springboot/stages/springboot-container
pwd
ls -la
```

次をまとめて確認できます。

```bash
test -f pom.xml && echo "pom.xml: OK"
test -f Dockerfile && echo "Dockerfile: OK"
test -f docker-compose.yml && echo "docker-compose.yml: OK"
test -f .env && echo ".env: OK"
```

表示されない項目があれば、そのファイルが不足しています。

### `failed to read dockerfile`または`Dockerfile: no such file`

Windowsの設定によっては、実際には`Dockerfile.txt`として保存されていることがあります。

```bash
ls -la Dockerfile*
```

正しい名前は、大文字の`D`で始まり、拡張子のない`Dockerfile`です。

### YAMLのエラー

次で構文を確認します。

```bash
docker compose config --quiet
```

よくある原因は次のとおりです。

- 字下げにタブを使っている
- `services:`、`db:`、`app:`の字下げが崩れている
- `:`を全角で入力している
- コピー時に行が抜けている

[第3章の`docker-compose.yml`全文](./03-container-handson.md#5-docker-composeymlを作る)と比較してください。

## 4. `.env`のエラー

### `Set ... in .env`または`required variable ... is missing`

`.env`がないか、必要な値が空です。

```bash
test -f .env && echo ".env: OK"
```

表示されない場合は、次のコマンドで`.env`を作成します。

```bash
code .env
```

[第3章の`.env`全文](./03-container-handson.md#6-環境変数ファイルを作る)を記述してください。必要な変数名は次の6個です。

```text
MARIADB_DATABASE
MARIADB_USER
MARIADB_PASSWORD
MARIADB_ROOT_PASSWORD
APP_SEED_ADMIN_PASSWORD
APP_SEED_USER_PASSWORD
```

確認時に値そのものを表示したくない場合は、変数名だけを表示します。

```bash
cut -d= -f1 .env
```

修正後に構文を再確認します。

```bash
docker compose config --quiet
```

`.env`では`=`の前後に空白を入れません。また、この研修では記号による解釈の違いを避けるため、教材に示した英数字の研修用パスワードを使用します。

## 5. 8081番ポートを使用できない

### `port is already allocated`または`address already in use`

別のSpring Bootアプリやコンテナが8081番を使用しています。Git Bashから次を実行します。

```bash
powershell.exe -NoProfile -Command "Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object LocalAddress,LocalPort,State,OwningProcess"
docker ps --format "table {{.Names}}\t{{.Ports}}"
```

以前にターミナルから起動したSpring Bootアプリがある場合は、そのターミナルで`Ctrl + C`を押して停止します。別のComposeプロジェクトが使用している場合は、そのプロジェクトのフォルダで`docker compose down`を実行します。

使用中のアプリを停止できない場合は、`docker-compose.yml`の次の行を変更できます。

変更前:

```yaml
ports:
  - "127.0.0.1:8081:8080"
```

変更後:

```yaml
ports:
  - "127.0.0.1:8082:8080"
```

再起動します。

```bash
docker compose up -d
```

この場合、ブラウザでは`http://localhost:8082/login`を開きます。右側の8080はコンテナ内の番号なので変更しません。

## 6. MariaDBがhealthyにならない

まず状態とログを確認します。

```bash
docker compose ps
docker compose logs --tail=200 db
```

起動直後の`health: starting`は異常とは限りません。初回はDBファイルを作るため時間がかかります。しばらくしても`unhealthy`になる場合は、コンテナ内部からhealthcheckと同じ確認を行います。

```bash
docker compose exec db sh -lc 'mariadb-admin ping -h 127.0.0.1 -uroot -p"$MARIADB_ROOT_PASSWORD" --silent'
```

`mysqld is alive`と表示されればMariaDBは応答しています。

よくある原因は次のとおりです。

- `.env`のDBパスワードが空
- `docker-compose.yml`のhealthcheckを正しくコピーできていない
- Docker Desktopへ割り当てられたメモリやディスク容量が不足
- 以前のvolumeを作ったときのパスワードと、現在の`.env`が異なる

最後のケースでは、[「パスワードを変更したのにログインできない」](#10-パスワードを変更したのにログインできない)を確認してください。

## 7. アプリがMariaDBへ接続できない

`db`はhealthyなのに`app`が停止・再起動する場合は、アプリログを確認します。

```bash
docker compose ps -a
docker compose logs --tail=200 app
```

ログに`Connection refused`、`Access denied`、`Unknown host`などがある場合は、`docker-compose.yml`の設定を確認します。

```yaml
DB_URL: jdbc:mariadb://db:3306/${MARIADB_DATABASE:-attendance}?useUnicode=true&characterEncoding=utf8
DB_USER: ${MARIADB_USER:-attendance_app}
DB_PASSWORD: ${MARIADB_PASSWORD:?Set MARIADB_PASSWORD in .env}
```

確認ポイント:

- ホスト名が`localhost`ではなく`db`になっている
- ポートがMariaDBの`3306`になっている
- `DB_USER`と`MARIADB_USER`が同じ値を参照している
- `DB_PASSWORD`と`MARIADB_PASSWORD`が同じ値を参照している
- `depends_on`に`condition: service_healthy`がある

修正後は作り直します。

```bash
docker compose up -d --force-recreate
docker compose ps
```

### JARをコピーできずimage作成に失敗する

`COPY --from=build ... not found`と表示される場合は、DockerfileのJAR名と`pom.xml`が一致していません。

```bash
grep -n -A1 "<artifactId>attendance-management-container</artifactId>" pom.xml
grep -n "attendance-management-container-0.0.1-SNAPSHOT.jar" Dockerfile
```

この教材の正しいJAR名は次です。

```text
attendance-management-container-0.0.1-SNAPSHOT.jar
```

## 8. ユーザー作成などの画面操作で403になる

ユーザーの作成・更新・削除後に、次のような画面が表示される場合があります。

```text
Whitelabel Error Page
type=Forbidden, status=403
```

この403は、画面に埋め込まれたCSRFトークンと、ブラウザのセッションが一致しない場合に発生します。別のSpring Bootアプリも`localhost`で開いていると、ポート番号が異なっていても、同じ名前のセッションCookieがブラウザ内で上書きされることがあります。

この教材の完成版では、ほかのアプリと衝突しない専用のCookie名`ATTENDANCE_CONTAINER_SESSION`を使用します。古いimageを使用している場合は、最初に再ビルドします。

```bash
docker compose up -d --build
```

続けて、次の順番で新しいセッションを作ります。

1. 403が表示されたタブを閉じる
2. `http://localhost:8081/login`を開き直す
3. `admin`でログインし直す
4. 「アカウント管理」からユーザーを作成する

再ログイン前に開いていた古い入力画面は送信せず、ログイン後に「新規作成」を開き直してください。

> CSRFを無効にすると403を回避できるように見えますが、画面からの不正な操作を防ぐ機能まで失われます。この教材ではCSRFを無効にしません。

## 9. Javaの変更が反映されない

まず、編集したファイルと文字列を確認します。

```bash
pwd
grep -n "本日の出勤を受け付けました" src/main/java/com/shinesoft/attendance/web/HomeController.java
```

次に、`--build`を付けて起動します。

```bash
docker compose up -d --build
docker compose logs --tail=100 app
```

それでも古い表示になる場合は、ブラウザを再読み込みし、`app`が新しく作り直されているか確認します。

```bash
docker compose ps
docker compose images
```

Dockerのbuild cacheを使わずに作り直すのは、通常の`--build`で解決しない場合だけです。

```bash
docker compose build --no-cache app
docker compose up -d --force-recreate app
docker compose logs --tail=100 app
```

また、同じ日にすでに`user1`で出勤済みの場合は、「出勤」ボタンが表示されません。次のどちらかを行います。

- 管理者画面で新しい一般ユーザーを作り、そのユーザーでログインする
- 消してよい研修データだけなら、[第4章の初期化手順](./04-operation-and-rebuild.md#4-研修データを初期状態へ戻す)を実行する

## 10. パスワードを変更したのにログインできない

`.env`の値は、既存データのパスワードを毎回上書きする設定ではありません。

- `MARIADB_*`は、新しいDB volumeを最初に初期化するときに使われる
- `APP_SEED_*`は、同名の初期ユーザーがまだ存在しないときに使われる
- volumeが残っている状態で`.env`だけを変更しても、既存パスワードは変わらない

一般ユーザーのパスワードは、管理者のアカウント管理画面から適切に変更してください。

初期パスワードを忘れ、DB内の研修データをすべて削除してよい場合だけ、volumeを初期化できます。

> **警告:** 次の操作はユーザーと勤怠記録をすべて削除し、元に戻せません。`pwd`と対象を必ず確認してください。

```bash
pwd
docker compose ps
docker compose down -v
docker compose up -d
```

新しいvolumeでは、現在の`.env`にあるパスワードで`admin`と`user1`が作られます。

## 11. ダウンロードやMavenビルドに失敗する

Docker Hubからimageを取得できるか、個別に確認します。

```bash
docker pull mariadb:11.4
docker pull maven:3.9.9-eclipse-temurin-17
docker pull eclipse-temurin:17-jre
```

Maven処理の詳しい出力を見るには、次を実行します。

```bash
docker compose build --progress=plain app
```

よくある原因:

- 一時的にインターネット接続が切れている
- 会社や研修会場のプロキシ設定が必要
- VPNやセキュリティソフトがDockerの通信を止めている
- Docker Desktopのディスク空き容量が不足

ネットワークやプロキシ設定を自分で変更できない場合は、表示されたエラーを記録して研修担当者へ連絡してください。

## 12. Docker Hubへpushできない

### `unauthorized`または`authentication required`

Docker Hubのログイン状態を確認します。

```bash
echo "$DOCKERHUB_USERNAME"
docker login --username "$DOCKERHUB_USERNAME"
```

`Password:`にはDocker Hubのアカウントパスワードではなく、作成済みのPersonal Access Tokenを入力します。入力中に文字が表示されなくても、そのままEnterキーを押します。

`docker login`へ`--username`を付けない場合は、ブラウザを使うdevice code flowが始まることがあります。この教材ではPersonal Access Tokenを使うため、`--username`を付けます。

### `requested access to the resource is denied`または`insufficient_scope`

よくある原因は次のとおりです。

- image名の先頭が自分のDocker Hubユーザー名になっていない
- Docker Hubの表示名やメールアドレスをnamespaceとして使っている
- リポジトリ名が`attendance-management`と一致していない
- Personal Access Tokenに書き込み権限がない
- 別のDocker Hubアカウントでログインしている

ユーザー名を修正した場合は、もう一度tagを付けてpushします。

```bash
docker tag attendance-management:1.0.0 "$DOCKERHUB_USERNAME/attendance-management:1.0.0"
docker push "$DOCKERHUB_USERNAME/attendance-management:1.0.0"
```

### `tag does not exist`

pushしようとした完全な名前が、PC上のimageに付いていません。第5章の手順5（`docker build`）と手順6（`docker tag`）を順番どおり実行したか確認します。

```bash
docker image ls
```

`YOUR_DOCKERHUB_USERNAME`という文字をそのまま使用せず、実際のDocker Hubユーザー名へ置き換えてください。

### Docker Hub画面に`1.0.0`が表示されない

次を確認します。

- 自分のnamespaceにある`attendance-management`を開いている
- `Tags`画面を開いてから、ブラウザを再読み込みした
- `docker push`の最後に`digest: sha256:...`が表示された
- `latest`ではなく`1.0.0`を確認している

この演習では`1.0.0`だけをpushするため、`latest`が表示されなくても正常です。

### Personal Access Tokenを誤って共有した

Tokenをコマンド、画面共有、チャットなどへ貼った場合は、ターミナル履歴や投稿を消すだけでは不十分です。Docker HubのPersonal Access Tokens画面で該当Tokenを直ちに無効化または削除し、新しいTokenへ交換してください。

> Personal Access Tokenは、エラー確認のためであっても画面共有、チャット、コマンド履歴へ貼り付けません。

## 13. 起動と停止で迷ったとき

| やりたいこと | コマンド | DBデータ |
| --- | --- | --- |
| 状態を見る | `docker compose ps` | 変化なし |
| ログを見る | `docker compose logs --tail=100 app` | 変化なし |
| 初めてビルドして起動する | `docker compose up -d --build` | 作成される |
| 通常起動する | `docker compose up -d` | 引き継ぐ |
| Java変更後に起動する | `docker compose up -d --build` | 引き継ぐ |
| 通常停止する | `docker compose down` | 残る |
| DBも完全初期化する | `docker compose down -v` | **削除される** |

通常は`docker compose down`を使います。`down -v`は、削除してよい研修データを明示的に初期化するときだけ使用してください。

## 14. 解決しない場合に共有する情報

研修担当者へ相談するときは、秘密情報を除き、次を共有します。

```bash
docker --version
docker compose version
docker compose ps -a
docker compose logs --tail=100 app
docker compose logs --tail=100 db
```

あわせて、次を伝えます。

- どのコマンドを実行したか
- どの段階で失敗したか
- ブラウザに表示されたURLとメッセージ
- 自分で変更したファイル名

`.env`の内容やパスワードは共有しません。

[前へ：Kubernetesマルチテナント演習](./07-kubernetes-multitenant-handson.md) ｜ [教材の入口へ戻る](./README.md)
