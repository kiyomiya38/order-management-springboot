# Day5（7/15）ログイン + 管理者機能 + テスト（Day4から拡張）

## 目的（Day5でできるようになること）
- ログイン機能（Spring Security）を実装できる
- 一般ユーザー / 管理者でアクセス権が分かれることを確認できる
- 管理者のアカウント管理・勤怠編集ができる
- `mvn test` でServiceテストを実行できる

## 前提
- Day4 を完了している
- `~/order-management-springboot/stages/day4` のトップ/一覧が動作する

---

## 0. 事前確認
```bash
java -version
mvn -version
git --version
```

---

## 1. 作業フォルダを準備（Day4を複製）
```bash
mkdir -p ~/order-management-springboot/stages/day5
cp -r ~/order-management-springboot/stages/day4/* ~/order-management-springboot/stages/day5/
cd ~/order-management-springboot/stages/day5
```

以降の `作成ファイル` は、`~/order-management-springboot` からのフルパスで表記します。  
例: `~/order-management-springboot/stages/day5/src/main/java/...`

---

## 2. ディレクトリを追加
```bash
mkdir -p ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config
mkdir -p ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form
mkdir -p ~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service
```

---

## 3. `pom.xml` を編集（依存追加）
作成ファイル: `~/order-management-springboot/stages/day5/pom.xml`

Day4からの追加依存:
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`（testスコープ）

確認コマンド:
```bash
rg -n "spring-boot-starter-security|spring-boot-starter-validation|spring-boot-starter-test" pom.xml
```

---

## 4. Day5差分ファイルを作成（完成版ソースを転記）
Day5はファイル数が多いため、入力ミス防止のためにリポジトリ完成版 `~/order-management-springboot/src` から `~/order-management-springboot/stages/day5` へ転記します。  
（転記後に必ず中身を開いて読むこと）

```bash
cp ~/order-management-springboot/src/main/resources/application.yml ~/order-management-springboot/stages/day5/src/main/resources/application.yml

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/domain/User.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/domain/User.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/domain/AttendanceStatus.java

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/repository/UserRepository.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/repository/UserRepository.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/repository/AttendanceRepository.java

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/service/UserService.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/service/AttendanceService.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/AttendanceService.java

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/config/DataSeeder.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/DataSeeder.java

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/AuthController.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AuthController.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/HomeController.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/HomeController.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/AttendanceController.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AttendanceController.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/UserController.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/UserController.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java

cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/form/UserForm.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/UserForm.java
cp ~/order-management-springboot/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/form/AdminAttendanceForm.java

cp ~/order-management-springboot/src/main/resources/templates/login.html ~/order-management-springboot/stages/day5/src/main/resources/templates/login.html
cp ~/order-management-springboot/src/main/resources/templates/index.html ~/order-management-springboot/stages/day5/src/main/resources/templates/index.html
cp ~/order-management-springboot/src/main/resources/templates/attendances.html ~/order-management-springboot/stages/day5/src/main/resources/templates/attendances.html
cp ~/order-management-springboot/src/main/resources/templates/users.html ~/order-management-springboot/stages/day5/src/main/resources/templates/users.html
cp ~/order-management-springboot/src/main/resources/templates/user-form.html ~/order-management-springboot/stages/day5/src/main/resources/templates/user-form.html
cp ~/order-management-springboot/src/main/resources/templates/admin-attendances.html ~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendances.html
cp ~/order-management-springboot/src/main/resources/templates/admin-attendance-form.html ~/order-management-springboot/stages/day5/src/main/resources/templates/admin-attendance-form.html

cp ~/order-management-springboot/src/main/resources/static/styles.css ~/order-management-springboot/stages/day5/src/main/resources/static/styles.css

cp ~/order-management-springboot/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java ~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java
```

---

## 5. ファイル作成チェック（必須）
```bash
rg --files ~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance | sort
rg --files ~/order-management-springboot/stages/day5/src/main/resources/templates | sort
rg --files ~/order-management-springboot/stages/day5/src/test/java/com/shinesoft/attendance | sort
```

期待する追加カテゴリ:
- Security: `config/SecurityConfig.java`
- Auth: `web/AuthController.java`
- User管理: `web/UserController.java`, `service/UserService.java`, `web/form/UserForm.java`
- 管理者勤怠編集: `web/AdminAttendanceController.java`, `web/form/AdminAttendanceForm.java`
- 画面: `login.html`, `users.html`, `user-form.html`, `admin-attendances.html`, `admin-attendance-form.html`
- テスト: `AttendanceServiceTest.java`

---

## 6. 起動
```bash
cd ~/order-management-springboot/stages/day5
mvn spring-boot:run
```

---

## 7. 動作確認

### 7-1. ログイン
1. `http://localhost:8080/login`
2. `user1 / password` でログイン
3. トップ画面表示を確認

### 7-2. 一般ユーザー権限
1. `user1` ログイン中に `http://localhost:8080/users` へアクセス
2. 403（アクセス不可）になることを確認

### 7-3. 管理者権限
1. ログアウト
2. `admin / admin123` でログイン
3. `http://localhost:8080/users` で新規作成/編集/削除を確認
4. `http://localhost:8080/admin/attendances` で勤怠編集を確認

---

## 8. テスト
```bash
mvn test
```

確認:
- `AttendanceServiceTest` の2テストが成功する

---

## 9. コード読解ポイント（必須）

1. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `requestMatchers` でURL別権限制御
- `formLogin` でログイン画面指定

2. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/service/UserService.java`
- パスワードハッシュ化
- ユーザー名重複チェック

3. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `Principal` からログインユーザー取得
- `isAdmin` で画面表示を分岐

4. `~/order-management-springboot/stages/day5/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
- 管理者だけが勤怠編集できる流れ

---

## 10. つまずきポイント
- ログインできない:
  - `DataSeeder` が作成されているか
  - `users` テーブルに `admin` / `user1` があるか（H2で確認）
- 管理画面が403:
  - `admin` でログインしているか
  - `ROLE_ADMIN` が設定されているか
- テストが失敗:
  - `pom.xml` に `spring-boot-starter-test` があるか
- `org.springframework.security...` や `jakarta.validation...` が「存在しません」と出る:
  - 原因は依存キャッシュ不整合の可能性が高い
  - Git Bashで次を実行して依存を再取得する
  - `cd ~/order-management-springboot/stages/day5`
  - `rm -rf ~/.m2/repository/org/springframework/security`
  - `rm -rf ~/.m2/repository/jakarta/validation`
  - `rm -rf ~/.m2/repository/org/hibernate/validator`
  - `mvn -U clean spring-boot:run`

---

## 11. 時間割目安
- 午前: Security + ログイン + 役割分離（120分）
- 午後: 管理者機能 + テスト + まとめ（150分）
