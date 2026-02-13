# 勤怠管理システム（MVP / 研修用）

1週間研修向けに、最小構成で動く勤怠管理システムを用意しています。  
UIはThymeleaf、DBはH2、ログインありです。

## 前提
- Java 17
- Maven 3.9+
- Git Bash（コマンド実行用）
- VS Code（編集用）

## クイックスタート
### 完成版を起動（`src` を使用）
```bash
cd ~/order-management-springboot
mvn spring-boot:run
```

### 停止
- 起動ターミナルで `Ctrl + C`

## Day別コード（段階学習用）
Dayごとに独立して起動できるコードを用意しています。  
```
stages/day1
stages/day2
stages/day3
stages/day4
stages/day5
```
各日で以下のように起動します（`dayX` を `day1` 〜 `day5` に置き換え）。
```bash
cd ~/order-management-springboot/stages/dayX
mvn spring-boot:run
```

Day別の目的:
- Day1: 最小MVCで画面表示
- Day2: 出勤 + DB保存（JPA/H2）
- Day3: 退勤 + 業務ルール
- Day4: 一覧画面 + H2確認
- Day5: ログイン/権限/管理者機能 + テスト

研修資料（カリキュラム）:
- Day0: `docs/curriculum/day0/day0.md`
- Day1: `docs/curriculum/day1/day1.md`
- Day2: `docs/curriculum/day2/day2.md`
- Day3: `docs/curriculum/day3/day3.md`
- Day4: `docs/curriculum/day4/day4.md`
- Day5: `docs/curriculum/day5/day5.md`

## 画面（ブラウザ）
- ログイン: `http://localhost:8080/login`
- 勤怠トップ: `http://localhost:8080/`
- 勤怠一覧: `http://localhost:8080/attendances`
- アカウント管理（管理者のみ）: `http://localhost:8080/users`
- 勤怠管理（管理者のみ）: `http://localhost:8080/admin/attendances`

アクセス制御:
- 未ログイン時は `/login` にリダイレクト
- `user1` は管理画面（`/users`, `/admin/attendances`）へアクセス不可（403）
- `admin` は管理画面へアクセス可能

初期ユーザー:
- admin / admin123（管理者）
- user1 / password（一般）

## 業務ルール（抜粋）
- 同日に複数回の「出勤」は不可
- 「退勤」は「出勤中」の場合のみ可能
- 退勤後に再度退勤不可
- 1ユーザーにつき1日1件

## H2コンソール（devのみ）
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:attendance`
- User: `sa`
- Password: （空）

補足:
- デフォルトプロファイルは `dev`（`application.yml` で `SPRING_PROFILES_ACTIVE:dev`）
- `prod` で起動した場合は H2コンソールは無効
- H2はインメモリDBのため、**アプリ再起動でデータは消えます**

## 環境変数（主な設定）
Git Bashでの設定例:
```bash
export APP_NAME=attendance-management
export SERVER_PORT=8080
export LOG_LEVEL=INFO
export SPRING_PROFILES_ACTIVE=dev
export DB_URL='jdbc:h2:mem:attendance;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
export DB_USER=sa
export DB_PASSWORD=
```

主なキー:
- `APP_NAME`: アプリ名
- `SERVER_PORT`: 待受ポート
- `LOG_LEVEL`: ログレベル
- `SPRING_PROFILES_ACTIVE`: `dev` / `prod`
- `DB_URL`, `DB_USER`, `DB_PASSWORD`: DB接続設定

## テスト
Day5のコードで実行してください。
```bash
cd ~/order-management-springboot/stages/day5
mvn test
```

## プロジェクト構成（抜粋）
```
src/main/java/com/shinesoft/attendance
  ├─ config
  ├─ domain
  ├─ exception
  ├─ repository
  ├─ service
  └─ web
src/main/resources
  ├─ templates
  └─ static
```

## アプリケーション詳細（構成・技術）

### アプリケーション概要
このシステムは「勤怠管理の最小構成（MVP）」です。  
受講者が **出勤→退勤→一覧確認** の一連の流れを体験し、  
業務ルール（同日複数出勤不可・未出勤退勤不可など）が  
Service層で制御されることを理解できるように設計しています。

### 主要フロー（出勤→退勤→一覧）
```
ログイン
  ↓
勤怠トップ（状態確認）
  ↓ 出勤
出勤中（開始時刻記録）
  ↓ 退勤
退勤済み（終了時刻記録）
  ↓ 一覧
勤怠一覧で履歴確認
```

### 使用技術
- 言語: Java 17、HTML、CSS
- フレームワーク: Spring Boot 3.x
- テンプレート: Thymeleaf
- DB: H2（インメモリ）
- ビルド: Maven

### ディレクトリと役割
```
src/main/java/com/shinesoft/attendance
  ├─ AttendanceManagementApplication.java  # 起動クラス
  ├─ config
  │   ├─ DataSeeder.java                   # 初期ユーザー投入
  │   └─ SecurityConfig.java               # ログイン/権限制御
  ├─ domain
  │   ├─ User.java                         # ユーザー
  │   ├─ Attendance.java                   # 勤怠レコード
  │   └─ AttendanceStatus.java             # 勤怠状態（未出勤/出勤中/退勤済み）
  ├─ exception
  │   └─ BusinessException.java            # 業務ルール違反用の例外
  ├─ repository
  │   ├─ UserRepository.java               # UserのDB操作
  │   └─ AttendanceRepository.java         # AttendanceのDB操作
  ├─ service
  │   ├─ AttendanceService.java            # 出勤/退勤など業務ロジック
  │   └─ UserService.java                  # ユーザー管理ロジック
  └─ web
      ├─ HomeController.java               # トップ画面
      ├─ AttendanceController.java         # 一覧画面
      ├─ UserController.java               # アカウント管理画面
      ├─ AdminAttendanceController.java    # 管理者の勤怠編集
      ├─ AuthController.java               # ログイン画面
      └─ form
          ├─ UserForm.java                 # ユーザー入力
          └─ AdminAttendanceForm.java      # 勤怠編集入力

src/main/resources
  ├─ application.yml                       # 共通設定
  ├─ application-dev.yml                   # dev設定（H2 console）
  ├─ application-prod.yml                  # prod設定
  ├─ templates
  │   ├─ login.html                        # ログイン画面
  │   ├─ index.html                        # 勤怠トップ
  │   ├─ attendances.html                  # 勤怠一覧
  │   ├─ users.html                        # アカウント管理一覧
  │   └─ user-form.html                    # アカウント作成/編集
  │   └─ admin-attendances.html            # 管理者勤怠一覧
  │   └─ admin-attendance-form.html        # 管理者勤怠編集
  └─ static
      └─ styles.css                        # 画面共通CSS

src/test/java/com/shinesoft/attendance/service
  └─ AttendanceServiceTest.java            # 出勤成功/二重出勤NGのテスト
```

## よくあるエラーと対処
- `mvn: command not found`
  - MavenのPATH設定を確認し、ターミナルを再起動
- `there is no POM in this directory`
  - `pwd` と `ls` で `pom.xml` があるフォルダにいるか確認
- `org.springframework.security... が存在しません`
  - 依存追加漏れを確認し、`mvn -U clean spring-boot:run` を実行
- 文字化け/BOM関連エラー
  - UTF-8（BOMなし）で保存する
