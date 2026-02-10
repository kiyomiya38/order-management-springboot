# Day4（7/14）設定と環境差分（profile / H2 / ログ）

## 目的（Day4でできるようになること）
- `application.yml` の役割が分かる
- dev / prod プロファイルで挙動が変わることを説明できる
- H2コンソールでDBを確認できる

---

## 1. application.yml を確認
ファイル:
- `src/main/resources/application.yml`

ポイント:
- `spring.datasource.url`（H2設定）
- `spring.thymeleaf.cache`（devはfalse）
- `spring.application.name`

---

## 2. プロファイル切替
### dev
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

### prod
```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run
```

---

## 3. H2コンソール（devのみ）
URL:
```
http://localhost:8080/h2-console
```
JDBC URL:
```
jdbc:h2:mem:attendance
```

テーブル:
- `users`
- `attendances`

---

## 4. チェックポイント
- dev で H2 console に入れる
- prod では H2 console が無効になる

---

## 5. 時間割目安
- 午前: 設定確認(60分) / プロファイル(60分)
- 午後: H2確認(60分) / まとめ(30分)
