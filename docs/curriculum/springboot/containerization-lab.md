# Spring Boot コンテナ化演習（App + MariaDB / Docker Compose）

## 目的
- `~/order-management-springboot/src` のアプリを、受講生自身でコンテナ化できるようになる
- `app` コンテナ + `db`（MariaDB）コンテナの2コンテナ構成を作成する
- `Dockerfile` / `docker-compose.yml` / `.dockerignore` を自分で作成し、`docker compose up -d` で起動できるようにする
- DBデータを Docker Volume で永続化する

この演習はローカル環境で行います（HTTPSは扱いません）。

---

## 1. 構成

### 1-1. コンテナ構成
| サービス | 役割 | コンテナ名 | ポート |
|---|---|---|---|
| app | Spring Bootアプリ | `app` | `8080:8080` |
| db | MariaDB | `db` | 外部公開なし（内部3306） |

### 1-2. 接続イメージ
1. ブラウザ -> `http://localhost:8080/login`
2. `app` -> `db:3306`（Compose内部ネットワーク）
3. DBデータは `db_data` volume に永続化

---

## 2. 事前準備（ローカルPC側）

### 2-1. 前提コマンド確認
```bash
docker -v
docker compose version
```

### 2-2. 作業フォルダへ移動
```bash
cd ~/order-management-springboot
pwd
ls
```

期待:
- `Dockerfile`, `docker-compose.yml`, `pom.xml`, `src` が見える

---

## 3. ファイル作成・編集（受講生作業）

### 3-1. `pom.xml` に MariaDB JDBC ドライバを追加
対象: `~/order-management-springboot/pom.xml`

`<dependencies>` に次を追加:

```xml
<dependency>
  <groupId>org.mariadb.jdbc</groupId>
  <artifactId>mariadb-java-client</artifactId>
  <scope>runtime</scope>
</dependency>
```

補足:
- H2依存は残して問題ありません（ローカル学習用）

### 3-2. `Dockerfile` を作成（マルチステージ）
対象: `~/order-management-springboot/Dockerfile`

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package spring-boot:repackage

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/target/attendance-management-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

### 3-3. `docker-compose.yml` を作成
対象: `~/order-management-springboot/docker-compose.yml`

```yaml
services:
  db:
    image: mariadb:11.4
    container_name: db
    environment:
      MARIADB_DATABASE: attendance
      MARIADB_USER: attendance_app
      MARIADB_PASSWORD: ChangeMe_Strong_123!
      MARIADB_ROOT_PASSWORD: ChangeMe_Root_123!
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
    container_name: app
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
      DB_URL: jdbc:mariadb://db:3306/attendance?useUnicode=true&characterEncoding=utf8
      DB_USER: attendance_app
      DB_PASSWORD: ChangeMe_Strong_123!
      DB_DRIVER: org.mariadb.jdbc.Driver
      SHOW_SQL: "false"
    restart: unless-stopped

volumes:
  db_data:
```

---

## 4. ビルド・起動

### 4-1. 構文チェック
```bash
docker compose config
```

### 4-2. イメージビルド + 起動
```bash
docker compose up -d --build
docker compose ps
```

期待:
- `db` が `healthy`
- `app` が `Up`

### 4-3. ログ確認
```bash
docker compose logs -f app
```

---

## 5. 動作確認

### 5-1. HTTP応答確認
```bash
curl -I http://localhost:8080/login
```

### 5-2. ブラウザ確認
`http://localhost:8080/login` を開き、ログイン画面が表示されることを確認。

---

## 6. 永続化確認（重要）

### 6-1. DBデータ作成
画面操作でユーザー追加など、何らかのデータを登録。

### 6-2. コンテナ再作成後も残るか確認
```bash
docker compose down
docker compose up -d
```

再度画面を開き、登録データが残っていれば `db_data` 永続化は成功。

補足:
- `docker compose down -v` は volume を削除するため、データは消えます。

---

## 7. トラブルシュート

### 症状: `app` が `no main manifest attribute` で落ちる
原因:
- 実行可能JARではなく通常JARが作成されている
- `spring-boot:repackage` がビルドに含まれていない

確認:
```bash
docker compose logs app
```

対処:
- `Dockerfile` のビルドコマンドを次にする  
  `mvn -B -DskipTests clean package spring-boot:repackage`
- `docker compose build --no-cache app` で再ビルド

### 症状: `app` が DB接続エラーで落ちる
原因:
- `pom.xml` に MariaDBドライバ追加漏れ
- `DB_URL` / `DB_USER` / `DB_PASSWORD` の不一致
- `db` が `healthy` になる前に接続している

確認:
```bash
docker compose logs app
docker compose logs db
```

### 症状: `localhost:8080` にアクセスできない
確認:
```bash
docker compose ps
docker compose logs app
```
原因:
- `app` が再起動ループ
- ポート競合（既に8080を別プロセスが使用）

---

## 8. この演習と実運用の差分

この演習は「初学者が確実に動かす」ことを優先しています。  
実運用では次を追加検討します。

1. DBパスワードを平文で持たない（Secret管理）
2. マルチステージの最終イメージをより小さく・脆弱性対策
3. ヘルスチェック強化（アプリの `/actuator/health` など）
4. 監視・アラート・バックアップ
5. Kubernetes向けマニフェスト分離（ConfigMap/Secret/Deployment/Service）

---

## 9. 完了条件
- `docker compose up -d --build` で `app` / `db` が起動する
- `http://localhost:8080/login` にアクセスできる
- `db_data` により再起動後もデータが残る
- 主要エラー（manifest/DB接続）を自力で切り分けられる

ここまでできれば、次のKubernetesデプロイ演習に進む準備が整っています。
