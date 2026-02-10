# 勤怠管理システム（Day3 / 研修用）

1週間研修向けに、最小構成で動く勤怠管理システムを用意しています。  
Day3では「退勤」と業務ルールを実装します（ログインなし）。

## 前提
- Java 17
- Maven 3.9+

## 起動
```bash
mvn spring-boot:run
```

## 画面（ブラウザ）
- 勤怠トップ: `http://localhost:8080/`

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

## テスト
Day3では未実装。

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
- 言語: Java 17
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
