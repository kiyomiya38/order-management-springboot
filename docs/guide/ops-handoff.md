# 後続研修への仕込み（Docker/CI/CD/K8s引き継ぎメモ）

このアプリは後続のコンテナ化・CI/CD・Kubernetes研修で同じコードを使う前提で作成されています。

## 設計方針（意図）
- **設定を環境変数で上書きできる**ように `application.yml` に集約
- **ログは標準出力**に出る設定（logback想定）
- **DB接続情報は外出し**できる形（将来 RDS/外部DBへ差し替え）
- **ポートは環境変数で変更可能**（`SERVER_PORT`）

## 重要な設定キー（ConfigMap/Secret想定）
- `APP_NAME` : アプリ名（レスポンスにも反映）
- `LOG_LEVEL` : ログレベル切替
- `SPRING_PROFILES_ACTIVE` : dev / prod 切替
- `DB_URL` / `DB_USER` / `DB_PASSWORD` / `DB_DRIVER`

## Docker化のポイント
- 起動は `mvn spring-boot:run` ではなく `java -jar` に切替予定
- `SERVER_PORT` はコンテナ内のEXPOSE/Serviceと連携
- `LOG_LEVEL` は環境変数で動的に変えられる

### Dockerfile（雛形）
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/attendance-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### docker-compose.yml（雛形）
```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      APP_NAME: attendance-container
      LOG_LEVEL: INFO
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:h2:mem:attendance;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
      DB_USER: sa
      DB_PASSWORD: ""
```

### docker build/run 手順
```bash
mvn -q -DskipTests package
```

```bash
docker build -t attendance-management:local .
```

```bash
docker run --rm -p 8080:8080 \
  -e APP_NAME=attendance-container \
  -e LOG_LEVEL=INFO \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:h2:mem:attendance;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE" \
  -e DB_USER=sa \
  -e DB_PASSWORD= \
  attendance-management:local
```

### docker compose 手順
```bash
docker compose up --build
```

## CI/CDでの利用イメージ
- build: `mvn -q -DskipTests package`
- test: （必要に応じて追加）
- deploy: K8sのConfigMap/Secretで値を注入

## 将来の拡張
- 外部DBへの切り替え（DB_URLなど）
- `/actuator/health` 追加でK8sヘルスチェック
- `Dockerfile` / `docker-compose.yml` の追加
