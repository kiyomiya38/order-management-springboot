# 03 Spring BootとMariaDBをコンテナ化する

[前へ：完成版アプリの確認](./02-completed-app-overview.md) ｜ [次へ：起動後の操作と再ビルド](./04-operation-and-rebuild.md)

この章では、完成済みのSpring BootアプリとMariaDBをDockerで起動します。Javaの機能はすでに完成しているため、Javaコードは作りません。受講者が作るのは、コンテナ化に必要な次の4ファイルです。

- `Dockerfile`
- `.dockerignore`
- `docker-compose.yml`
- `.env`

最終的には、次の1コマンドでアプリとデータベースを起動します。

```bash
docker compose up -d --build
```

> この教材のコマンドは、Windows 11上のGit Bashで実行します。Docker Desktopを先に起動しておいてください。

## 1. 最初に覚えるDocker用語

| 用語 | 初学者向けの説明 | この教材での例 |
| --- | --- | --- |
| image（イメージ） | コンテナを作るための、変更されないひな形 | Javaアプリのimage、MariaDBのimage |
| container（コンテナ） | imageから作られ、実際に動いている環境 | `app`、`db` |
| Dockerfile | Javaアプリのimageをどう作るかを書いた手順書 | MavenでJARを作り、Java 17で実行する |
| Compose | 複数のコンテナをまとめて定義・操作する仕組み | `app`と`db`を一緒に起動する |
| volume | コンテナを作り直してもデータを残す保管場所 | MariaDBの`db_data` |
| 環境変数 | 実行時に外から渡す設定値 | DB名、DBパスワード |
| network | コンテナ同士が通信するための専用ネットワーク | `app`から`db:3306`へ接続する |

今回の全体像は次のとおりです。

```text
ブラウザ
   |
   | http://localhost:8081
   v
appコンテナ（完成済みSpring Bootアプリ）
   |
   | jdbc:mariadb://db:3306/attendance
   v
dbコンテナ（MariaDB）
   |
   v
db_data volume（DBデータを保存）
```

`app`から見た`localhost`は、`app`コンテナ自身です。そのため、MariaDBの接続先にはComposeのサービス名である`db`を使います。Composeは、同じファイルに書いたサービスが通信できるネットワークを自動で作ります。

## 2. 作業場所を確認する

Git Bashを開き、受講用プロジェクトへ移動します。

```bash
cd ~/order-management-springboot/stages/springboot-container
pwd
ls
```

`pwd`の末尾が次のようになっていることを確認します。

```text
/order-management-springboot/stages/springboot-container
```

さらに、完成版アプリの`pom.xml`と`src`があることを確認します。

```bash
test -f pom.xml && echo "pom.xml: OK"
test -d src && echo "src: OK"
```

両方の`OK`が表示されてから進みます。

## 3. `Dockerfile`を作る

プロジェクト直下に、拡張子なしの`Dockerfile`を作成します。VS Codeを使用する場合は、次のコマンドで開けます。

```bash
code Dockerfile
```

`code`コマンドが見つからない場合は、VS Codeの「ファイル」から`stages/springboot-container`フォルダを開き、画面左側のエクスプローラーで`Dockerfile`を新規作成してください。以降のファイルも同じ方法で作成できます。

`Dockerfile`の内容を、次の全文にします。

```dockerfile
# 第1段階: MavenとJDKを使って、Javaアプリをビルドする
# 「AS build」で、この段階にbuildという名前を付ける
FROM maven:3.9.9-eclipse-temurin-17 AS build

# これ以降の作業場所を、コンテナ内の/workspaceにする
WORKDIR /workspace

# Mavenの設定ファイルと、Java・HTMLなどのソースをコピーする
COPY pom.xml ./
COPY src ./src

# ソースコードをコンパイルし、実行可能JARをtargetフォルダへ作る
RUN mvn -B clean package

# 第2段階: JREだけを使って、完成したアプリを実行する
# ビルド用のMavenやソースコードは、この最終imageには含まれない
FROM eclipse-temurin:17-jre

# アプリを実行する場所を、コンテナ内の/appにする
WORKDIR /app

# Javaが日本時間を使うように設定する
ENV TZ=Asia/Tokyo
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Tokyo"

# 第1段階のbuildで作ったJARだけを、app.jarという名前でコピーする
COPY --from=build /workspace/target/attendance-management-container-0.0.1-SNAPSHOT.jar ./app.jar

# このアプリがコンテナ内で8080番ポートを使うことを示す
# PC側へ公開するポート番号は、docker-compose.ymlで設定する
EXPOSE 8080

# rootではなく、権限を抑えたユーザーID 10001でアプリを動かす
USER 10001

# コンテナを起動したときに、java -jar /app/app.jarを実行する
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

`#`で始まる行は説明用のコメントです。Dockerはその行を処理しないため、コメントも含めてそのままコピーできます。

保存したら、ファイル名を確認します。

```bash
ls -l Dockerfile
```

`Dockerfile.txt`になっている場合は正しくありません。ファイル名は必ず`Dockerfile`です。

### Dockerfileの読み方

このDockerfileは、2段階でimageを作ります。

```text
第1段階 build
  MavenとJDKが入ったimage
  → ソースコードをコンパイル
  → 実行可能JARを作る

第2段階 実行
  Java実行環境だけが入ったimage
  → 第1段階からJARだけをコピー
  → java -jarで起動
```

この方式をマルチステージビルドと呼びます。ビルド用ツールを最終imageへ入れずに済むため、役割が分かりやすくなります。詳しくは[Docker公式: Multi-stage builds](https://docs.docker.com/build/building/multi-stage/)を参照してください。

主な行の意味は次のとおりです。

| 記述 | 意味 |
| --- | --- |
| `FROM ... AS build` | MavenとJava 17を使うビルド段階を始める |
| `WORKDIR /workspace` | 以降の作業場所を決める |
| `COPY` | PC上のファイルをimage作成用の環境へコピーする |
| `RUN mvn ... package` | 実行可能JARを作る |
| `FROM eclipse-temurin:17-jre` | Java 17で実行する第2段階を始める |
| `ENV` | 日本時間を使うように設定する |
| `USER 10001` | rootではないユーザー番号でアプリを動かす |
| `ENTRYPOINT` | コンテナ開始時にJARを実行する |

`USER 10001`は、アプリを強い権限を持つrootとして動かさないための補助設定です。Linuxの権限設計はこの講義の範囲外なので、番号を暗記する必要はありません。

`COPY --from=build`に書いたJAR名は、`pom.xml`の`artifactId`と`version`から決まります。この教材では次の組み合わせです。

```text
artifactId: attendance-management-container
version:    0.0.1-SNAPSHOT
JAR名:      attendance-management-container-0.0.1-SNAPSHOT.jar
```

## 4. `.dockerignore`を作る

Dockerはimageを作るとき、現在のフォルダをbuild contextとして読み込みます。不要なファイルや秘密情報を読み込ませないために、`.dockerignore`を作ります。

```bash
code .dockerignore
```

`.dockerignore`の内容を、次の全文にします。

```dockerignore
.git
.gitignore
.env
target
data
docs
README.md
```

`.env`を含めているのは、後ほど設定するパスワードをimageへ取り込まないためです。

## 5. `docker-compose.yml`を作る

`Dockerfile`は1つのimageの作り方を示します。一方、`docker-compose.yml`には、JavaアプリとMariaDBをどのように組み合わせて動かすかを書きます。

```bash
code docker-compose.yml
```

`docker-compose.yml`の内容を、次の全文にします。YAMLでは字下げにタブを使わず、半角スペースを使ってください。

```yaml
# 起動するコンテナをservicesの中に定義する
services:
  # dbは、勤怠データを保存するMariaDBのサービス
  db:
    # MariaDB 11.4の公式imageを使う
    image: mariadb:11.4

    # MariaDBの初期データベース名・ユーザー名・パスワードを設定する
    # 値は、後で作成する.envファイルから受け取る
    environment:
      MARIADB_DATABASE: ${MARIADB_DATABASE:-attendance}
      MARIADB_USER: ${MARIADB_USER:-attendance_app}
      MARIADB_PASSWORD: ${MARIADB_PASSWORD:?Set MARIADB_PASSWORD in .env}
      MARIADB_ROOT_PASSWORD: ${MARIADB_ROOT_PASSWORD:?Set MARIADB_ROOT_PASSWORD in .env}

    # DBのデータをdb_data volumeへ保存し、コンテナを停止しても残す
    volumes:
      - db_data:/var/lib/mysql

    # MariaDBが接続を受け付けられる状態か、10秒ごとに確認する
    healthcheck:
      test: ["CMD-SHELL", "mariadb-admin ping -h 127.0.0.1 -uroot -p$${MARIADB_ROOT_PASSWORD} --silent"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s

    # PCやDockerを再起動した場合も、手動で停止していなければ再起動する
    restart: unless-stopped

  # appは、完成版Spring Bootアプリのサービス
  app:
    # 現在のフォルダにあるDockerfileからimageを作る
    build:
      context: .
      dockerfile: Dockerfile

    # dbのhealthcheckが成功してからappを起動する
    depends_on:
      db:
        condition: service_healthy

    # PCの127.0.0.1:8081を、appコンテナの8080番へ接続する
    # 127.0.0.1に限定するため、同じPCのブラウザからだけアクセスできる
    ports:
      - "127.0.0.1:8081:8080"

    # Spring Bootへ、使用する設定やDBの接続情報を渡す
    environment:
      APP_NAME: attendance-container
      LOG_LEVEL: INFO
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: 8080
      SERVER_ADDRESS: 0.0.0.0

      # ホスト名dbは、上で定義したdbサービスを表す
      DB_URL: jdbc:mariadb://db:3306/${MARIADB_DATABASE:-attendance}?useUnicode=true&characterEncoding=utf8
      DB_USER: ${MARIADB_USER:-attendance_app}
      DB_PASSWORD: ${MARIADB_PASSWORD:?Set MARIADB_PASSWORD in .env}
      DB_DRIVER: org.mariadb.jdbc.Driver
      SHOW_SQL: "false"

      # 初回起動時に、研修用の管理者と一般ユーザーを登録する
      APP_SEED_ENABLED: "true"
      APP_SEED_ADMIN_PASSWORD: ${APP_SEED_ADMIN_PASSWORD:?Set APP_SEED_ADMIN_PASSWORD in .env}
      APP_SEED_USER_PASSWORD: ${APP_SEED_USER_PASSWORD:?Set APP_SEED_USER_PASSWORD in .env}

    # アプリに不要な書き込みや権限を与えないための設定
    read_only: true
    tmpfs:
      - /tmp
    security_opt:
      - no-new-privileges:true

    # PCやDockerを再起動した場合も、手動で停止していなければ再起動する
    restart: unless-stopped

# servicesから使用する名前付きvolumeを定義する
volumes:
  db_data:
```

YAMLでも、`#`で始まる行は説明用のコメントです。Docker Composeはその行を設定として処理しないため、コメントも含めてそのままコピーできます。

### `db`サービス

- `mariadb:11.4`という公式imageからMariaDBコンテナを作ります。
- DBデータを`db_data` volumeへ保存します。
- `healthcheck`で、MariaDBが接続を受け付けられる状態か確認します。

### `app`サービス

- 先ほどの`Dockerfile`からSpring Bootアプリのimageを作ります。
- PC自身を表す`127.0.0.1`の8081番と、コンテナの8080番を接続します。
- `SPRING_PROFILES_ACTIVE: prod`により、MariaDB用の設定を使います。
- DBのホスト名には、Composeのサービス名`db`を使います。
- `read_only`、`tmpfs`、`no-new-privileges`により、アプリが必要以上の書き込みや権限を持たないようにします。

この3つは安全性を補うために用意した設定です。詳しいLinuxファイルシステムや権限設計はこの講義では扱わないため、ここでは全文どおり使用し、「不要な書き込みや権限を減らす設定」と理解できれば十分です。

ポートを`127.0.0.1`へ限定しているため、この教材のアプリは同じPCのブラウザからだけアクセスできます。研修用パスワードを使うローカル演習なので、ほかのPCへ公開しません。

`depends_on`の`service_healthy`は、単にDBコンテナの処理が始まった時点ではなく、healthcheckが成功してからアプリを起動する指定です。詳しくは[Docker公式: Control startup order](https://docs.docker.com/compose/how-tos/startup-order/)を参照してください。

## 6. 環境変数ファイルを作る

パスワードを`docker-compose.yml`へ直接書かず、`.env`から渡します。プロジェクト直下に`.env`を作成します。

```bash
code .env
```

`.env`の内容を、次の全文にします。

```dotenv
MARIADB_DATABASE=attendance
MARIADB_USER=attendance_app
MARIADB_PASSWORD=TrainingDbPass2026
MARIADB_ROOT_PASSWORD=TrainingRootPass2026
APP_SEED_ADMIN_PASSWORD=TrainingAdminPass2026
APP_SEED_USER_PASSWORD=TrainingUserPass2026
```

この値はローカル研修専用です。実際のサービス用パスワードとして使用してはいけません。

自分で値を変更する場合は、`=`の前後に空白を入れず、6項目すべてを設定してください。

`.env`はパスワードを含むため、Gitへ登録したり、内容を画面共有やチャットへ貼ったりしません。また、`docker compose config`を`--quiet`なしで実行すると展開後の値が表示されるため注意してください。Composeの環境変数については[Docker公式: Interpolation](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/)も参照してください。

## 7. 作成したファイルを確認する

```bash
ls -la Dockerfile .dockerignore docker-compose.yml .env
```

次の4ファイルが表示されれば準備完了です。

```text
Dockerfile
.dockerignore
docker-compose.yml
.env
```

Composeの構文と必須環境変数を確認します。

```bash
docker compose config --quiet
```

成功時は何も表示されず、次のプロンプトへ戻ります。エラーが出た場合は、起動せずに[第8章 トラブルシューティング](./08-troubleshooting.md)を確認してください。

## 8. imageを作り、コンテナを起動する

Docker Desktopが`Engine running`になっていることを確認し、次を実行します。

```bash
docker compose up -d --build
```

この1コマンドで、主に次の処理が行われます。

1. MariaDBのimageを取得する
2. Dockerfileを使ってMavenビルドを行う
3. Spring Bootアプリのimageを作る
4. `db_data` volumeと専用networkを作る
5. `db`を起動する
6. `db`がhealthyになってから`app`を起動する

初回はimageやMavenライブラリをダウンロードするため、数分かかることがあります。

## 9. 起動状態とログを確認する

```bash
docker compose ps
```

目安として、次の状態を確認します。

- `db`が`Up`かつ`healthy`
- `app`が`Up`
- `app`のPORTSに`127.0.0.1:8081->8080`が表示される

起動直後は`db`が`health: starting`の場合があります。その場合は少し待ってから、もう一度`docker compose ps`を実行します。

アプリの直近のログも確認します。

```bash
docker compose logs --tail=100 app
```

最後の方に、コンテナ内の8080番でSpring Bootが起動したことを示すログがあり、繰り返しエラーが表示されていなければ次へ進みます。ログ表示を終了するために`Ctrl + C`を押す必要はありません。このコマンドは100行を表示すると自動で終了します。

## 10. ブラウザからログインする

ブラウザで次を開きます。

```text
http://localhost:8081/login
```

管理者でログインします。

| 項目 | 値 |
| --- | --- |
| ユーザー名 | `admin` |
| パスワード | `.env`の`APP_SEED_ADMIN_PASSWORD`。教材の値は`TrainingAdminPass2026` |

一般ユーザーも用意されています。

| 項目 | 値 |
| --- | --- |
| ユーザー名 | `user1` |
| パスワード | `.env`の`APP_SEED_USER_PASSWORD`。教材の値は`TrainingUserPass2026` |

ログイン後に「今日の勤怠」画面が表示されれば、Spring BootコンテナからMariaDBコンテナへの接続にも成功しています。

## 11. この章の完了チェック

- [ ] 作業場所が`stages/springboot-container`になっている
- [ ] `Dockerfile`を全文どおり作成した
- [ ] `.dockerignore`を作成した
- [ ] `docker-compose.yml`を全文どおり作成した
- [ ] `.env`を全文どおり作成した
- [ ] `docker compose config --quiet`が成功した
- [ ] `docker compose up -d --build`が成功した
- [ ] `db`がhealthy、`app`がUpになった
- [ ] `admin`と`user1`でログインできた
- [ ] `app`が`db`という名前でMariaDBへ接続する理由を説明できる

次章では、主な画面機能と代表的なAPIの確認、volumeによるデータ保持、Javaの表示メッセージ変更と再ビルドを行います。

[前へ：完成版アプリの確認](./02-completed-app-overview.md) ｜ [次へ：起動後の操作と再ビルド](./04-operation-and-rebuild.md)
