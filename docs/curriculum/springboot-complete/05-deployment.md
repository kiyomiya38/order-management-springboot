# H2からMariaDBへ切り替えてDocker Composeで起動する

この章では、開発用H2で完成させた同じアプリをMariaDBへ接続し、Spring BootとDBをDocker Composeで起動します。

完成版:

- [`Dockerfile`](../../../complete/Dockerfile)
- [`docker-compose.yml`](../../../complete/docker-compose.yml)
- [`.dockerignore`](../../../complete/.dockerignore)
- [`.env.example`](../../../complete/.env.example)
- [`application.yml`](../../../complete/src/main/resources/application.yml)
- [`application-dev.yml`](../../../complete/src/main/resources/application-dev.yml)
- [`application-prod.yml`](../../../complete/src/main/resources/application-prod.yml)
- [`V1__create_tables.sql`](../../../complete/src/main/resources/db/migration/V1__create_tables.sql)
- [`V2__add_index_to_attendance_work_date.sql`](../../../complete/src/main/resources/db/migration/V2__add_index_to_attendance_work_date.sql)

## 1. 構成

```text
ブラウザ
   |
   | http://localhost:8080
   v
appコンテナ（Spring Boot / prod profile）
   |
   | jdbc:mariadb://db:3306/attendance
   v
dbコンテナ（MariaDB）
   |
   v
db_data volume
```

Compose内部では、`localhost` ではなくサービス名 `db` でMariaDBへ接続します。`localhost` は各コンテナ自身を指すためです。

### 最初に覚えるDocker用語

| 用語 | この章での意味 |
| --- | --- |
| image | コンテナを作るための読み取り専用のひな形 |
| container | imageから起動した実行中の環境 |
| service | Composeで定義する`app`や`db`という単位 |
| volume | コンテナを作り直しても残すデータ領域 |
| port | ホストの8080番とappの8080番をつなぐ番号 |
| healthcheck | DBが接続可能になったか確認する仕組み |

```text
ソースコード
  → docker build
  → app image
  → docker compose up
  → app container

MariaDB container
  → db_data volumeへデータを保存
```

## 2. 前提確認

Docker Desktopを起動した後、Git Bashで実行します。

```bash
java -version
mvn -version
docker version
docker compose version
```

確認事項:

- Java 17
- MavenがJava 17を使用
- DockerのClientとServerの両方が表示される
- `docker compose` が使用できる

ターミナルをプロジェクトルートへ移動します。完成版を使う場合:

```bash
cd complete
pwd
test -f pom.xml && echo "project root: OK"
```

受講者自身の完成物を使う場合は、`practice/springboot-complete-handson`へ移動し、同じように`pom.xml`を確認します。

## 3. まずH2で基準状態を確認する

devが既定プロファイルなので、環境変数を指定せず起動するとH2を使います。

```bash
mvn spring-boot:run
```

確認:

- `http://localhost:8080/login` が表示される
- `admin / admin123` と `user1 / password` でログインできる
- `http://localhost:8080/h2-console` が利用できる
- `data/attendance` にH2ファイルが作成される

アプリを `Ctrl + C` で停止します。

この状態を基準にしてからMariaDBへ切り替えると、アプリの不具合と環境設定の不具合を分けて調査できます。

## 4. プロファイルによる切り替え

### 共通設定

[`application.yml`](../../../complete/src/main/resources/application.yml) には、DB製品に依存しない設定を置きます。

```yaml
spring:
  profiles:
    default: dev
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### dev

[`application-dev.yml`](../../../complete/src/main/resources/application-dev.yml) は、既定で次を使います。

- `jdbc:h2:file:./data/attendance;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE`
- H2 Console有効
- 初期ユーザー投入有効
- Thymeleafキャッシュ無効

### prod

[`application-prod.yml`](../../../complete/src/main/resources/application-prod.yml) は、次を環境変数から受け取ります。

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: ${DB_DRIVER:org.mariadb.jdbc.Driver}
```

prodではH2 Consoleを無効にし、初期ユーザー投入も既定で無効にします。値が不足したまま起動に失敗するのは、誤ったDBへ接続するより安全な動作です。

### 切り替えの要点

JavaコードとJARは同じです。次だけを実行環境から変えます。

| 環境 | Profile | DB | 設定元 |
| --- | --- | --- | --- |
| ローカル開発 | `dev` | H2 file | devの既定値 |
| Docker Compose | `prod` | MariaDB | Composeの環境変数 |

devでは`server.address=127.0.0.1`を既定にし、H2 Consoleと学習用アカウントをLANへ不用意に公開しません。コンテナではホストからポートへ接続できるよう、Composeが`SERVER_ADDRESS=0.0.0.0`を明示します。

## 5. Flywayを両方のDBで使う

起動順:

1. DataSourceを構成
2. FlywayがV1、V2を順番に適用
3. HibernateがEntityとスキーマを検証
4. DataSeederが有効な場合だけ初期ユーザーを投入
5. Webアプリがリクエストを受け付ける

H2は `MODE=MariaDB` を指定し、同じmigration SQLを開発とprodで使用します。ただし、互換モードがMariaDBの全仕様を再現するわけではありません。最終確認はMariaDBでも必要です。

運用ルール:

- 適用済みのV1、V2は編集しない
- 変更は `V3__説明.sql` のような新しいファイルで追加する
- `ddl-auto` を `update` や `create` に戻さない
- prodのDBを初期化して問題を隠さない

## 6. 実行可能JARを作る

```bash
mvn -DskipTests clean package
ls target
```

完成版のJAR:

```text
target/attendance-management-complete-0.0.1-SNAPSHOT.jar
```

devでJARを直接確認できます。

```bash
java -jar target/attendance-management-complete-0.0.1-SNAPSHOT.jar
```

確認後は `Ctrl + C` で停止します。

`spring-boot-maven-plugin` の `repackage` により、依存ライブラリと起動情報を含む実行可能JARになります。

## 7. Dockerfileを作る

プロジェクトルートへ [`Dockerfile`](../../../complete/Dockerfile) を作成します。

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/target/attendance-management-complete-0.0.1-SNAPSHOT.jar ./app.jar

ENV TZ=Asia/Tokyo
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Tokyo"

EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

確認ポイント:

- build段階にはMavenとJDKがある
- 実行段階にはJREとJARだけをコピーする
- image build中に実行可能JARを作る
- rootユーザーではなくUID `10001` で実行する
- OSのタイムゾーンとJVMの `user.timezone` をAsia/Tokyoへ揃える
- artifactIdやversionを変えたら、コピー元JAR名も変更する

このアプリは `LocalDate.now()` と `LocalDateTime.now()` を打刻に使います。コンテナの既定時刻がUTCのままだと、日本時間の深夜帯に勤務日がずれる可能性があります。Dockerfileの2つの `ENV` と、共通設定のHibernate JDBCタイムゾーンを揃えて確認します。

## 8. Docker Composeを作る

プロジェクトルートへ [`docker-compose.yml`](../../../complete/docker-compose.yml) を作成します。

```yaml
services:
  db:
    image: mariadb:11.4
    environment:
      MARIADB_DATABASE: ${MARIADB_DATABASE:-attendance}
      MARIADB_USER: ${MARIADB_USER:-attendance_app}
      MARIADB_PASSWORD: ${MARIADB_PASSWORD:?Set MARIADB_PASSWORD in .env}
      MARIADB_ROOT_PASSWORD: ${MARIADB_ROOT_PASSWORD:?Set MARIADB_ROOT_PASSWORD in .env}
    volumes:
      - db_data:/var/lib/mysql
    healthcheck:
      test: ["CMD-SHELL", "mariadb-admin ping -h 127.0.0.1 -uroot -p$${MARIADB_ROOT_PASSWORD} --silent"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s
    restart: unless-stopped

  app:
    build:
      context: .
      dockerfile: Dockerfile
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      APP_NAME: attendance-container
      LOG_LEVEL: INFO
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: 8080
      SERVER_ADDRESS: 0.0.0.0
      DB_URL: jdbc:mariadb://db:3306/${MARIADB_DATABASE:-attendance}?useUnicode=true&characterEncoding=utf8
      DB_USER: ${MARIADB_USER:-attendance_app}
      DB_PASSWORD: ${MARIADB_PASSWORD:?Set MARIADB_PASSWORD in .env}
      DB_DRIVER: org.mariadb.jdbc.Driver
      SHOW_SQL: "false"
      APP_SEED_ENABLED: "true"
      APP_SEED_ADMIN_PASSWORD: ${APP_SEED_ADMIN_PASSWORD:?Set APP_SEED_ADMIN_PASSWORD in .env}
      APP_SEED_USER_PASSWORD: ${APP_SEED_USER_PASSWORD:?Set APP_SEED_USER_PASSWORD in .env}
    read_only: true
    tmpfs:
      - /tmp
    security_opt:
      - no-new-privileges:true
    restart: unless-stopped

volumes:
  db_data:
```

`depends_on` のhealth条件により、MariaDBが接続可能になってからappを開始します。これは「DBが起動した」だけでなく、healthcheckが成功したことを条件にします。

appは読み取り専用ファイルシステムで動かし、一時ファイルだけを `/tmp` のtmpfsへ書きます。

## 9. `.dockerignore`、`.gitignore`と秘密情報

[`.dockerignore`](../../../complete/.dockerignore) には少なくとも次を含めます。

```dockerignore
.git
target
data
.env
```

ローカルのJAR、H2 DB、秘密情報をimage build contextへ送らないためです。

プロジェクトルートの`.gitignore`にも、少なくとも次を含めます。

```gitignore
target/
data/
.env
```

`.dockerignore`はDockerのbuild context、`.gitignore`はGitの追跡対象を制御します。片方だけでは代用できません。

[`.env.example`](../../../complete/.env.example) を `.env` へコピーします。

```bash
cp .env.example .env
```

`.env` の値を研修用パスワードへ変更します。初回は引用や変数展開で迷わないよう、英大文字・英小文字・数字を組み合わせた値を使います。

```dotenv
MARIADB_DATABASE=attendance
MARIADB_USER=attendance_app
MARIADB_PASSWORD=TrainingDbPass2026
MARIADB_ROOT_PASSWORD=TrainingRootPass2026
APP_SEED_ADMIN_PASSWORD=TrainingAdminPass2026
APP_SEED_USER_PASSWORD=TrainingUserPass2026
```

注意:

- `.env.example` は値の名前だけを共有する
- 実際の `.env` はGitへコミットしない
- 実運用のパスワードをチャット、画面共有、シェル履歴へ貼らない
- Composeの初期ユーザーパスワードはdev既定値ではなく、ここで設定した値になる
- `MARIADB_*`の初期化値は、新しい`db_data` volumeを初めて起動するときに使われる
- `APP_SEED_*`は、同名ユーザーがまだ存在しないときだけ新規ユーザーへ使われる
- 既存volumeのまま`.env`だけ変更しても、既存DBや既存ユーザーのパスワードは変わらない

Git管理対象外であることを確認します。

```bash
git check-ignore .env
```

`.env` が表示されればignore対象です。

## 10. 構文確認、ビルド、起動

秘密値を画面へ展開しないよう、quietで構文だけを確認します。

```bash
docker compose config --quiet
```

ビルドして起動します。

```bash
docker compose up -d --build
docker compose ps
```

期待:

- `db` が `healthy`
- `app` が `Up`
- appのポートが `0.0.0.0:8080->8080/tcp`

ログを確認します。

```bash
docker compose logs --tail=200 db
docker compose logs --tail=200 app
```

appログで確認するもの:

- prodプロファイル
- MariaDBへの接続
- FlywayのV1、V2適用、または「最新」
- Hibernateのschema validation成功
- Tomcatの8080番起動

## 11. MariaDBで動作確認する

HTTP:

```bash
curl -i http://localhost:8080/login
```

ブラウザ:

1. `http://localhost:8080/login` を開く
2. ユーザー名 `admin` と、`.env` の `APP_SEED_ADMIN_PASSWORD` でログイン
3. `user1` のパスワードは `.env` の `APP_SEED_USER_PASSWORD`
4. ユーザー作成、出勤、退勤、勤怠編集を確認

MariaDBへ接続します。既定のDB名とユーザー名を変更した場合は、コマンドも合わせます。

```bash
docker compose exec db mariadb -uattendance_app -p attendance
```

プロンプトで `.env` の `MARIADB_PASSWORD` を入力し、次を実行します。

```sql
SHOW TABLES;

SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT id, username, role FROM users;

SELECT id, user_id, work_date, status, start_time, end_time
FROM attendances
ORDER BY work_date DESC;
```

終了:

```sql
exit
```

## 12. データ永続化を確認する

画面から確認用ユーザーを1件作成し、コンテナを停止・再作成します。

```bash
docker compose down
docker compose up -d
docker compose ps
```

再ログインし、作成したユーザーが残っていれば `db_data` volumeによる永続化は成功です。

`docker compose down` はvolumeを残します。  
`docker compose down -v` はDB volumeを削除し、研修データを復元不能にします。完全初期化を明示的に行うときだけ使用してください。

## 13. ソース変更を反映する

Java、テンプレート、migrationを変更した場合:

```bash
mvn -DskipTests clean package
docker compose up -d --build
docker compose logs --tail=200 app
```

Dockerfileや依存関係のキャッシュを疑う場合だけ:

```bash
docker compose build --no-cache app
docker compose up -d
```

通常は毎回 `--no-cache` を使う必要はありません。

DBスキーマ変更は新しいmigrationとして追加します。既存volumeがある状態で新しいバージョンが一度だけ適用されることを確認してください。

## 14. 停止

データを残して停止:

```bash
docker compose down
```

実行状態とログだけ確認:

```bash
docker compose ps
docker compose logs --tail=100 app
```

## 15. この構成と実運用の差

このCompose構成は研修用です。実運用では、少なくとも次を別途設計します。

- TLS終端とHTTPS
- Secret Managerなどによる秘密情報管理
- DBバックアップ、復元手順、migration前の退避
- アプリのhealth endpointと監視・通知
- ログ収集、相関ID、監査ログ
- imageの脆弱性検査と更新方針
- DBを外部へ公開しないネットワーク制御
- 複数インスタンスでの同時打刻と排他制御
- 初期ユーザー投入ではなく正式なユーザー発行手順

## 16. 完了条件

- devではH2、prodではMariaDBへ接続する理由を説明できる
- `docker compose config --quiet` が成功する
- `docker compose up -d --build` でdbとappが起動する
- MariaDBのFlyway履歴にV1、V2が記録される
- 画面とAPIの主要機能がMariaDBでも動く
- `docker compose down` 後もデータが残る
- パスワードをGitへ含めていない
- appがDBへ `db:3306` で接続する理由を説明できる

起動できない場合は、設定を何度も変更する前に [troubleshooting.md](./troubleshooting.md) の順序で切り分けます。
