# 勤怠管理システム完成版

講義資料と空プロジェクトからの実装手順は、[Spring Boot完成版コース](../docs/curriculum/springboot-complete/README.md)を参照してください。

## 開発環境（H2）

```bash
mvn spring-boot:run
```

- URL: `http://localhost:8080`
- 管理者: `admin` / `admin123`
- 一般ユーザー: `user1` / `password`
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/attendance;MODE=MariaDB;DB_CLOSE_ON_EXIT=FALSE`

上記パスワードは `dev` プロファイル専用です。

## テスト

```bash
mvn clean verify
```

## MariaDB + Docker Compose

Git Bash:

```bash
cp .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

`.env`の全placeholderを研修環境用の値へ変更してから実行します。

```bash
docker compose config --quiet
docker compose up --build
```

`prod` プロファイルは `DB_URL`、`DB_USER`、`DB_PASSWORD` を必須とし、
FlywayでMariaDBへ同じマイグレーションを適用します。
コンテナ内のJVMは、勤怠日と打刻時刻を合わせるため`Asia/Tokyo`で動作します。

## コース全体の検証

リポジトリルートから実行します。

```powershell
.\scripts\verify-springboot-complete.ps1
```
