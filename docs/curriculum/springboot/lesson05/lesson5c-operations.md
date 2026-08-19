# Lesson5C 動作確認・プロファイル・参照整合性

[Lesson5共通準備と全体目次](./lesson5.md)、[Lesson5A](./lesson5a-authentication.md)、[Lesson5B](./lesson5b-management.md)を完了してから実施します。

## 目的

- ログイン状態とロールによる画面アクセスの違いをブラウザで確認できる
- ユーザー作成の処理をControllerからRepositoryまで追跡できる
- `dev` / `prod` プロファイルの設定差を説明できる
- 勤怠履歴があるユーザーの削除をServiceで禁止できる

## 前提

- Lesson5Bのユーザー管理と勤怠管理が動作する
- `~/order-management-springboot/stages/lesson05` で `mvn compile` が成功する

---

## 1. ファイル作成チェック（必須）

起動前に、Lesson5で追加・編集したファイルが指定パスに存在することを確認します。

バックエンド短縮コースでは、次の点も確認します。

- 提供されたHTML/CSS/JavaScriptが指定パスに存在する
- 提供コード内の説明コメントが削除されていない
- `templates` と `static` を取り違えていない
- `users.js` の実装内容は評価対象にせず、画面から読み込まれていることを確認する

```bash
find ~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance -type f | sort
find ~/order-management-springboot/stages/lesson05/src/main/resources/templates -type f | sort
find ~/order-management-springboot/stages/lesson05/src/main/resources/static -type f | sort
```

期待する追加カテゴリ:

- Security: `config/SecurityConfig.java`
- Auth: `web/AuthController.java`
- User管理: `web/UserController.java`, `service/UserService.java`, `web/form/UserForm.java`
- 管理者勤怠編集: `web/AdminAttendanceController.java`, `web/form/AdminAttendanceForm.java`
- 画面: `login.html`, `users.html`, `user-form.html`, `admin-attendances.html`, `admin-attendance-form.html`
- JavaScript: `static/users.js`

この確認の目的は、必須ファイルの作成漏れを起動前に見つけることです。Lesson5は依存追加とファイル追加が多いため、問題が起きたときは直前に作成したファイルから順番に確認してください。

---

## 2. 起動

```bash
cd ~/order-management-springboot/stages/lesson05
mvn spring-boot:run
```

ターミナルに `Started AttendanceManagementApplication` が表示され、`http://localhost:8080/login` を開けることを確認します。

---

## 3. ブラウザでの動作確認

認証は「誰であるかを確認すること」、認可は「その利用者に操作を許可すること」です。ログインの有無とロールを変えながら、結果の違いを確認します。

### 3-1. ログインしていない場合

1. シークレットウィンドウなど、ログイン情報が残っていないブラウザを開く
2. `http://localhost:8080/users` へ直接アクセスする
3. ログイン画面へ移動することを確認する

ログインしていない利用者は、認証が必要なURLを直接開けません。

### 3-2. 一般ユーザーでログインする

1. `http://localhost:8080/login` を開く
2. `user1 / password` でログインする
3. トップ画面にログインユーザー名が表示されることを確認する
4. 出勤、退勤、本人の勤怠一覧を操作できることを確認する
5. `http://localhost:8080/users` へ直接アクセスする
6. `403 Forbidden` になり、管理画面を操作できないことを確認する
7. `http://localhost:8080/admin/attendances` でも同じ結果になることを確認する

`user1` は認証済みですが、`ROLE_ADMIN` を持たないため管理URLは許可されません。

### 3-3. 管理者でログインする

1. `user1` からログアウトする
2. `admin / admin123` でログインする
3. `http://localhost:8080/users` を開けることを確認する
4. ユーザーの新規作成と編集を行い、一覧へ反映されることを確認する
5. 削除ボタンを押すと確認ダイアログが表示され、キャンセル時は削除されないことを確認する
6. 検索欄とロール選択で、一覧が画面遷移なしに絞り込まれることを確認する
7. `http://localhost:8080/admin/attendances` を開き、勤怠を編集できることを確認する

確認結果は次の表に整理できます。

| 状態 | `/` | `/users` | `/admin/attendances` |
| --- | --- | --- | --- |
| 未ログイン | ログイン画面へ移動 | ログイン画面へ移動 | ログイン画面へ移動 |
| `user1` | 表示できる | 403 | 403 |
| `admin` | 表示できる | 表示・操作できる | 表示・操作できる |

---

## 4. コード読解ポイント（必須）

### 4-1. ファイルごとの責務を確認する

1. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
   - `requestMatchers` でURL別の権限制御を行う
   - `formLogin` でログイン画面を指定する
2. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/UserService.java`
   - パスワードをハッシュ化する
   - ユーザー名の重複を確認する
   - 勤怠履歴があるユーザーの削除を拒否する
3. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/HomeController.java`
   - `Principal` からログインユーザーを取得する
   - `isAdmin` で画面表示を分岐する
4. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
   - 管理者用の勤怠一覧と編集画面を制御する
5. `~/order-management-springboot/stages/lesson05/src/main/resources/static/users.js`
   - 削除確認ダイアログと一覧絞り込みを補助する

### 4-2. ユーザー作成をコードから追跡する

コードを変更せず、管理者がユーザー作成画面を送信したときの処理を追います。次の表をノートまたは研修記録へ記入してください。

| 順番 | ファイル / メソッド | 受け取る値 | 判定・依頼する処理 | 次へ渡す値 |
| ---: | --- | --- | --- | --- |
| 1 | `SecurityConfig` | `POST /users`とログイン情報 | 管理者権限を確認 | 許可されたリクエスト |
| 2 | `UserController#create` | `UserForm` | `@Valid`の結果を確認 | username / password / role |
| 3 | `UserService#create` | 3つの入力値 | 業務バリデーション、重複確認、ハッシュ化 | `User` |
| 4 | `UserRepository` | username / `User` | 存在確認、DB保存 | 保存済み`User` |
| 5 | `UserController#create` | Serviceの処理結果 | 成功メッセージを設定 | `redirect:/users` |

正常系を追跡した後、既存ユーザー名を入力した場合も追跡します。

1. `@Valid`を通過できる入力でも、どこで重複を検出するか探す
2. `BusinessException`がどのメソッドへ戻るか探す
3. `binding.reject(...)`から画面のエラー表示までを探す
4. ControllerがRepositoryを直接呼ばない理由を説明する

合格条件:

- 上の表を、実際のメソッド名と値をソースコード上で指しながら説明できる
- 入力形式の検証と、ユーザー名重複という業務ルールの違いを説明できる
- 正常系と例外系で、最後に返す画面またはリダイレクト先がどう変わるか説明できる

---

## 5. つまずきポイント

- ログインできない:
  - `DataSeeder` が作成されているか
  - `users` テーブルに `admin` / `user1` があるかH2コンソールで確認する
- 管理画面が403:
  - `admin` でログインしているか
  - `ROLE_ADMIN` が設定されているか
- `http://localhost:8080/admin/attendances` で500（Whitelabel Error Page）:
  - `AttendanceRepository` の `findAllByOrderByWorkDateDesc()` / `findWithUserById()` に `@EntityGraph(attributePaths = "user")` があるか確認する
  - `open-in-view` を有効にして回避せず、画面で必要な関連をRepositoryで明示的に取得する
- `org.springframework.security...` や `jakarta.validation...` が「存在しません」と出る:
  - `pom.xml` に `spring-boot-starter-security` と `spring-boot-starter-validation` があるか確認する
  - Git Bashで `mvn -U clean compile` を実行し、依存関係を再取得する

---

## 6. 現行`src`との対応（必須）

Lesson5手順が完成版 `src` と同じ命名・構成になっていることを最終確認します。

一致確認（`stages/lesson05` と完成版 `src`）:

- `AttendanceService#getTodayAttendance(Long)`（`Attendance` または `null`）
- `AttendanceService#listAttendances(Long)`
- `AttendanceRepository#findByUser_IdAndWorkDate(...)`
- `AttendanceRepository#findByUser_IdOrderByWorkDateDesc(...)`
- `AttendanceRepository#findAllByOrderByWorkDateDesc()`
- `getAttendance(...)` は `findWithUserById(...)`、`updateAttendance(...)` は標準 `findById(...)` を利用

Lesson5本文は上記の完成版命名・構成に合わせて記載済みです。追加の読み替え作業は不要です。

確認ポイント:

- 「業務ルール（`clockIn` / `clockOut` / `updateAttendance`）」の本質が同じである
- 命名差分の吸収ではなく、同じ実装をそのまま読解できる状態になっている

---

## 7. `dev` / `prod` プロファイルの読み方（必須）

この章の目的:

- Lesson5作業ディレクトリを、完成版と同じ「共通設定 + プロファイル分離」に揃える
- Lesson5Aで`application.yml`へ追加した研修用初期ユーザー設定を、開発用`dev`だけへ移す
- 本番想定`prod`では、初期投入を環境変数で明示的に有効化しない限り動かさない

編集ファイル（`stages/lesson05`）:

- `~/order-management-springboot/stages/lesson05/src/main/resources/application.yml`
- `~/order-management-springboot/stages/lesson05/src/main/resources/application-dev.yml`
- `~/order-management-springboot/stages/lesson05/src/main/resources/application-prod.yml`

### 7-1. 共通設定

`application.yml` を以下に置き換えます。Lesson5Aで追加した`app.seed`は共通ファイルから外し、直後に作る`application-dev.yml`と`application-prod.yml`へ環境ごとの値として移動します。

```yaml
# 全環境共通の設定
spring:
  application:
    # アプリ名（未指定時は attendance-management）
    name: ${APP_NAME:attendance-management}
  profiles:
    # 起動時プロファイル（未指定時は dev）
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    # DB接続情報は環境変数で切替可能（未指定時はH2）
    url: ${DB_URL:jdbc:h2:mem:attendance;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}
    driver-class-name: ${DB_DRIVER:org.h2.Driver}
  jpa:
    hibernate:
      # Entity定義に合わせてテーブルを更新
      ddl-auto: update
    # 必要時のみSQLログを有効化
    show-sql: ${SHOW_SQL:false}
    # View描画中の追加DBアクセスを禁止し、必要データはService内で取得する
    open-in-view: false
  thymeleaf:
    # 画面キャッシュ設定はdev/prodで上書き
    cache: false

server:
  # ポートは環境変数で切替可能
  port: ${SERVER_PORT:8080}
  # VMでは127.0.0.1、コンテナでは0.0.0.0など環境に合わせて指定
  address: ${SERVER_ADDRESS:0.0.0.0}

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  name: ${APP_NAME:attendance-management}
```

### 7-2. 開発用設定

`application-dev.yml` を新規作成します。

```yaml
# 開発用プロファイル
spring:
  h2:
    console:
      # 開発中はH2コンソールを有効化
      enabled: true
      path: /h2-console
  thymeleaf:
    # 画面確認しやすいようにキャッシュOFF
    cache: false

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  seed:
    enabled: true
    admin-password: ${APP_SEED_ADMIN_PASSWORD:admin123}
    user-password: ${APP_SEED_USER_PASSWORD:password}
```

### 7-3. 本番想定設定

`application-prod.yml` を新規作成します。

```yaml
# 本番用プロファイル
spring:
  h2:
    console:
      # 本番ではH2コンソールを無効化
      enabled: false
  thymeleaf:
    # 本番ではキャッシュON
    cache: true

logging:
  level:
    root: ${LOG_LEVEL:INFO}

app:
  seed:
    enabled: ${APP_SEED_ENABLED:false}
    admin-password: ${APP_SEED_ADMIN_PASSWORD:}
    user-password: ${APP_SEED_USER_PASSWORD:}
```

### 7-4. プロファイル切替

一方の起動を終了してから、もう一方を起動してください。

```bash
cd ~/order-management-springboot/stages/lesson05

# 開発モード（未指定でもdev）
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# 本番モード（画面キャッシュON / H2コンソールOFF）
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

確認ポイント:

- `dev` と `prod` で挙動が変わるのは設定であり、Javaコードの分岐ではない
- `dev` ではH2コンソールが有効で、研修用初期ユーザーが投入される
- `prod` ではH2コンソールが無効で、初期投入も標準では無効になる
- 本番DBへの切替は `DB_URL` などの環境変数で行う

---

## 8. 参照整合性とユーザー削除（必須）

勤怠履歴があるユーザーは、意図しない履歴消失を防ぐため削除禁止とします。

背景:

- `Attendance` は `user_id` の必須参照（`@ManyToOne(optional = false)`）を持つ
- `AttendanceRepository#existsByUser_Id(...)` で削除前に関連データを確認する
- 関連がある場合は `BusinessException` として画面へ返す

確認手順:

1. `user1` でログインして出勤し、必要なら退勤する
2. `admin` でログインして`users`画面から`user1`を削除する
3. `勤怠履歴があるユーザーは削除できません` と表示されることを確認する
4. ユーザーと勤怠がどちらも残っていることを確認する

学習ポイント:

- 削除処理では、対象データだけでなく関連データが残っていないかも確認する
- 業務要件によって方針は変わる（削除禁止 / 論理削除 / 連鎖削除）
- このLessonでは、DB例外が発生する前にServiceで判定し、想定内の業務エラーとして扱う

---

## Lesson5C 完了条件

- 未ログイン、一般ユーザー、管理者でアクセス結果が変わることをブラウザで確認できる
- ユーザー作成をControllerからRepositoryまで追跡できる
- `dev` / `prod` プロファイルでH2コンソール、Thymeleafキャッシュ、初期ユーザー投入の差を説明できる
- 勤怠履歴があるユーザーの削除がServiceで拒否される
- 認証・認可・業務ルール・参照整合性をコード上で説明できる

完了後は [Lesson6 REST API基礎](../lesson06/lesson6.md) へ進みます。
