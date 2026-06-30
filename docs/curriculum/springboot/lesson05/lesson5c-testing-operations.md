# Lesson5C テスト・プロファイル・参照整合性

[Lesson5共通準備と全体目次](./lesson5.md)、[Lesson5A](./lesson5a-authentication.md)、[Lesson5B](./lesson5b-management.md)を完了してから実施します。

## 目的

- ServiceとSecurityのテストを実行し、回帰を検出できる
- `dev` / `prod` プロファイルの設定差を説明できる
- 勤怠履歴があるユーザーの削除をServiceで禁止できる
- Lesson5全体のコードをControllerからRepositoryまで追跡できる

## 前提

- Lesson5Bのユーザー管理と勤怠管理が動作する
- `~/order-management-springboot/stages/lesson05` で `mvn compile` が成功する

## 学習内容

### Lesson5C開始: テストと運用確認

Lesson5全体の通し番号を維持するため、最初の手順番号はLesson5BのPhase 3-7に続く「Phase 3-8」とします。

#### Phase 3-8: `AttendanceServiceTest.java` を発展版へ置き換える（状態遷移と時刻整合の回帰テスト）
作成ファイル:
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`

Lesson2で作成した最小テストを、以下のコードへ全文置き換えます。

このファイルを作る理由（先に読む）:
1. 目的:
   - `AttendanceService` の業務ルールが今後の修正で壊れていないかを自動確認するため
2. 何を守るテストか:
   - 1回目の出勤は成功する
   - 同日に2回目の出勤は失敗する（業務エラーになる）
3. いつ効くか:
   - `mvn test` を実行したときに毎回チェックされる
   - 手作業で毎回ブラウザ確認しなくても、最低限の品質を機械的に守れる
4. 初学者向けの理解:
   - テストは「機能を増やした後に、前に動いていたルールが壊れていないか」を確認する安全網

重要:
- 配置先は必ず `src/test/java` 配下にする（`src/main/java` ではない）
- `src/main/java` に置くと本番コード側として扱われ、テスト用として正しく運用しづらくなる

以下のコードへ全文置き換えてください。

```java
package com.shinesoft.attendance.service; // テスト対象Serviceのパッケージ

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals; // 値一致検証
import static org.junit.jupiter.api.Assertions.assertNotNull; // nullでないことの検証
import static org.junit.jupiter.api.Assertions.assertThrows; // 例外検証

import org.junit.jupiter.api.BeforeEach; // 各テスト前処理
import org.junit.jupiter.api.Test; // テストメソッド
import org.springframework.beans.factory.annotation.Autowired; // 依存注入
import org.springframework.boot.test.context.SpringBootTest; // Spring統合テスト
import org.springframework.security.crypto.password.PasswordEncoder; // テストデータ作成時に利用
import org.springframework.transaction.annotation.Transactional; // テスト後ロールバック

import com.shinesoft.attendance.domain.Attendance; // 勤怠エンティティ
import com.shinesoft.attendance.domain.AttendanceStatus; // 状態Enum
import com.shinesoft.attendance.domain.User; // ユーザーエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // テスト前掃除で使う
import com.shinesoft.attendance.repository.UserRepository; // テストユーザー取得で使う

@SpringBootTest // Springコンテナを立ち上げて実行
@Transactional // 各テストをトランザクションで実行
class AttendanceServiceTest {

    @Autowired
    private AttendanceService service; // テスト対象

    @Autowired
    private UserRepository userRepository; // テストデータ準備に使う

    @Autowired
    private AttendanceRepository attendanceRepository; // テーブル掃除に使う

    @Autowired
    private PasswordEncoder passwordEncoder; // ユーザー作成時の暗号化

    private Long userId; // テスト対象ユーザーID

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll(); // 勤怠を毎回空にする
        User user = userRepository.findByUsername("user1").orElseGet(() -> {
            User u = new User();
            u.setUsername("user1");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            return userRepository.save(u);
        });
        userId = user.getId();
    }

    @Test
    void clockIn_success() { // 正常系: 出勤できる
        Attendance attendance = service.clockIn(userId);
        assertEquals(AttendanceStatus.WORKING, attendance.getStatus());
        assertNotNull(attendance.getStartTime());
    }

    @Test
    void clockIn_twice_shouldFail() { // 異常系: 二重出勤は禁止
        service.clockIn(userId);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockIn(userId));
        assertEquals("すでに出勤済みです", ex.getMessage());
    }

    @Test
    void clockOut_beforeClockIn_shouldFail() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
        assertEquals("退勤するには先に出勤してください", ex.getMessage());
    }

    @Test
    void clockOut_twice_shouldFail() {
        service.clockIn(userId);
        service.clockOut(userId);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
        assertEquals("すでに退勤済みです", ex.getMessage());
    }

    @Test
    void updateAttendance_endBeforeStart_shouldFail() {
        Attendance attendance = service.clockIn(userId);
        LocalDate workDate = LocalDate.now();
        LocalDateTime start = workDate.atTime(9, 0);
        LocalDateTime end = workDate.atTime(8, 59);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            service.updateAttendance(
                attendance.getId(), userId, workDate, start, end, AttendanceStatus.FINISHED));
        assertEquals("終了時刻は開始時刻以降にしてください", ex.getMessage());
    }

    @Test
    void updateAttendance_startDateMustMatchWorkDate() {
        Attendance attendance = service.clockIn(userId);
        LocalDate workDate = LocalDate.now().minusDays(1);
        LocalDateTime start = LocalDate.now().atTime(9, 0);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            service.updateAttendance(
                attendance.getId(), userId, workDate, start, null, AttendanceStatus.WORKING));
        assertEquals("開始時刻の日付は勤務日と一致させてください", ex.getMessage());
    }
}
```

理解ポイント:
- 「壊れてはいけない業務ルール」をテストで固定する
- 出勤、退勤、管理者更新の6本を通し、主要な状態遷移と時刻整合を回帰確認する

完了チェック:
```bash
mvn compile
mvn test
```

詰まった時の比較方法:
- 例: `stages/lesson05` のファイルと完成版 `src` の同名ファイルを開き、差分を確認する
- 比較対象の優先度:
  1. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
  2. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/UserService.java`
  3. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`

---

## 5. ファイル作成チェック（必須）

バックエンド短縮コースでは、通常のチェックに加えて次を確認します。

- 提供されたHTML/CSS/JavaScriptが指定パスに存在する
- 提供コード内の説明コメントが削除されていない
- `templates` と `static` を取り違えていない
- `users.js` の実装内容は評価対象にせず、画面から読み込まれていることを確認する
```bash
find ~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance -type f | sort
find ~/order-management-springboot/stages/lesson05/src/main/resources/templates -type f | sort
find ~/order-management-springboot/stages/lesson05/src/main/resources/static -type f | sort
find ~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance -type f | sort
```

期待する追加カテゴリ:
- Security: `config/SecurityConfig.java`
- Auth: `web/AuthController.java`
- User管理: `web/UserController.java`, `service/UserService.java`, `web/form/UserForm.java`
- 管理者勤怠編集: `web/AdminAttendanceController.java`, `web/form/AdminAttendanceForm.java`
- 画面: `login.html`, `users.html`, `user-form.html`, `admin-attendances.html`, `admin-attendance-form.html`
- JavaScript: `static/users.js`
- テスト: `AttendanceServiceTest.java`

理解ポイント（5分）:
- このチェックの目的:
  - 必須ファイルの作成漏れを起動前に潰す
- 重要ポイント:
  - Lesson5は「依存追加 + 多数ファイル追加」のため、フェーズ完了ごとのチェックが最短ルート

---

## 6. 起動
```bash
cd ~/order-management-springboot/stages/lesson05
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
4. 削除ボタン押下時に確認ダイアログが出ることを確認（キャンセル時は削除されない）
5. `users` 画面の検索欄とロール選択で、一覧が画面遷移なしで絞り込まれることを確認
6. `http://localhost:8080/admin/attendances` で勤怠編集を確認

理解ポイント（15分）:
- この確認の目的:
  - 「認証」と「認可（権限）」を実際の挙動で区別して理解する
- 見るべき結果:
  - `user1` は管理URLが403
  - `admin` は管理URLへアクセス可能
- 実務との対応:
  - URL単位の権限制御は運用時の基本パターン

---

## 8. テスト
先に [lesson5-testing.md](./lesson5-testing.md) の追加テスト2ファイルを作成してください。

```bash
mvn test
```

確認:
- `AttendanceServiceTest` 6件、`UserServiceTest` 3件、`SecurityAccessTest` 3件の合計12件が成功する

理解ポイント（10分）:
- この確認の目的:
  - 主要業務ロジックが壊れていないことを自動で確認する
- 最低限見ること:
  - 出勤成功テスト
  - 二重出勤失敗テスト
  - 未出勤/再退勤の失敗テスト
  - 管理者更新時の勤務日・時刻前後関係テスト
- よくあるミス:
  - テスト依存漏れ、またはパッケージパス不一致でテストが検出されない

---

## 9. コード読解ポイント（必須）

### 9-1. ファイルごとの責務を確認する

1. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/config/SecurityConfig.java`
- `requestMatchers` でURL別権限制御
- `formLogin` でログイン画面指定

2. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/service/UserService.java`
- パスワードハッシュ化
- ユーザー名重複チェック

3. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/HomeController.java`
- `Principal` からログインユーザー取得
- `isAdmin` で画面表示を分岐

4. `~/order-management-springboot/stages/lesson05/src/main/java/com/shinesoft/attendance/web/AdminAttendanceController.java`
- 管理者だけが勤怠編集できる流れ

5. `~/order-management-springboot/stages/lesson05/src/main/resources/static/users.js`
- 削除確認ダイアログと一覧絞り込み（クライアント側UI改善）

### 9-2. ユーザー作成をコードから追跡する

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

## 10. つまずきポイント
- ログインできない:
  - `DataSeeder` が作成されているか
  - `users` テーブルに `admin` / `user1` があるか（H2で確認）
- 管理画面が403:
  - `admin` でログインしているか
  - `ROLE_ADMIN` が設定されているか
- `http://localhost:8080/admin/attendances` で500（Whitelabel Error Page）:
  - `AttendanceRepository` の `findAllByOrderByWorkDateDesc()` / `findWithUserById()` に `@EntityGraph(attributePaths = "user")` があるか確認する
  - `open-in-view` を有効にして回避せず、画面で必要な関連をRepositoryで明示的に取得する
- テストが失敗:
  - `pom.xml` に `spring-boot-starter-test` があるか
- `org.springframework.security...` や `jakarta.validation...` が「存在しません」と出る:
  - 原因は依存キャッシュ不整合の可能性が高い
  - Git Bashで次を実行して依存を再取得する
  - `cd ~/order-management-springboot/stages/lesson05`
  - `rm -rf ~/.m2/repository/org/springframework/security`
  - `rm -rf ~/.m2/repository/jakarta/validation`
  - `rm -rf ~/.m2/repository/org/hibernate/validator`
  - `mvn -U clean spring-boot:run`

---

## 11. 時間割目安
- 新人研修では2日構成とする
- 1日目: Security + ログイン（150分）/ ユーザー管理（150分）
- 2日目: 管理者勤怠（150分）/ Service・Securityテスト（120分）/ コード追跡（30分）/ プロファイル・振り返り（60分）
- 経験者が1日で実施する場合も、テストと参照整合の章は省略しない

バックエンド短縮コースでは画面コードの作成時間を削減しますが、Security、Service・Securityテスト、コード追跡、参照整合の章は省略しません。

---

## 12. 現行`src`との対応（必須）
この章を追加する理由（先に読む）:
- Lesson5手順が完成版 `src` と同じ命名・構成になっていることを最終確認するため

一致確認（`stages/lesson05` と 完成版 `src`）:
- `AttendanceService#getTodayAttendance(Long)`（`Attendance` または `null`）
- `AttendanceService#listAttendances(Long)`
- `AttendanceRepository#findByUser_IdAndWorkDate(...)`
- `AttendanceRepository#findByUser_IdOrderByWorkDateDesc(...)`
- `AttendanceRepository#findAllByOrderByWorkDateDesc()`
- `getAttendance(...)` は `findWithUserById(...)`、`updateAttendance(...)` は標準 `findById(...)` を利用

補足（重要）:
- Lesson5本文は、上記の完成版命名・構成に合わせて記載済み
- 追加の読み替え作業は不要

確認ポイント:
- 「業務ルール（`clockIn` / `clockOut` / `updateAttendance`）」の本質は同じ
- 命名差分の吸収ではなく、同じ実装をそのまま読解できる状態になっている

---

## 13. `dev` / `prod` プロファイルの読み方（必須）
この章を追加する理由（先に読む）:
- Lesson5作業ディレクトリを、完成版と同じ「共通設定 + プロファイル分離」に揃えるため

編集ファイル（`stages/lesson05`）:
- `~/order-management-springboot/stages/lesson05/src/main/resources/application.yml`
- `~/order-management-springboot/stages/lesson05/src/main/resources/application-dev.yml`
- `~/order-management-springboot/stages/lesson05/src/main/resources/application-prod.yml`

手順:
1. `application.yml` を以下に置き換える（共通設定）
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

2. `application-dev.yml` を新規作成する（開発用）
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

3. `application-prod.yml` を新規作成する（本番想定）
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

4. プロファイル切替を実行確認する（Git Bash）
```bash
cd ~/order-management-springboot/stages/lesson05

# 開発モード（未指定でもdev）
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# 本番モード（画面キャッシュON / H2コンソールOFF）
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

確認ポイント:
- `dev` と `prod` で挙動が変わるのは設定であり、Javaコード分岐ではない
- 本番DB切替は `DB_URL` など環境変数で行う

---

## 14. テスト追加演習（任意）
必修の6テストを通した後、境界条件を追加します。

編集ファイル:
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/service/UserServiceTest.java`
- `~/order-management-springboot/stages/lesson05/src/test/java/com/shinesoft/attendance/web/SecurityAccessTest.java`
- （完成版読解のみの場合）`~/order-management-springboot/src/test/java/com/shinesoft/attendance/service/AttendanceServiceTest.java`

追加候補:
1. `NOT_STARTED` に時刻を入れて更新すると失敗する
2. 同一ユーザー・同一日付へ更新すると失敗する
3. 勤怠履歴があるユーザーを削除すると失敗する

サンプル（1）:
```java
@Test
void updateAttendance_notStartedWithTimes_shouldFail() {
    Attendance attendance = service.clockIn(userId);

    BusinessException ex = assertThrows(BusinessException.class, () ->
        service.updateAttendance(
            attendance.getId(),
            userId,
            LocalDate.now(),
            LocalDateTime.now(),
            null,
            AttendanceStatus.NOT_STARTED
        )
    );
    assertEquals("未出勤の時刻は空にしてください", ex.getMessage());
}
```

補足:
- 上記を追加する場合は `LocalDate` / `LocalDateTime` の `import` 追加が必要
- 最終確認は `mvn test`

---

## 15. 参照整合とユーザー削除（必須）
勤怠履歴があるユーザーは、意図しない履歴消失を防ぐため削除禁止とします。

背景:
- `Attendance` は `user_id` の必須参照（`@ManyToOne(optional = false)`）を持つ
- `AttendanceRepository#existsByUser_Id(...)` で削除前に関連データを確認する
- 関連がある場合は `BusinessException` として画面へ返す

確認手順:
1. `user1` でログインして出勤（必要なら退勤）を実行
2. `admin` でログインして `users` 画面から `user1` を削除
3. `勤怠履歴があるユーザーは削除できません` と表示され、ユーザーと勤怠が残ることを確認

学習ポイント:
- 削除APIは「対象データがあるか」だけでなく「関連データが残っていないか」も確認が必要
- 業務要件によって方針は変わる（削除禁止 / 論理削除 / 連鎖削除）
- このLessonでは、DB例外が発生する前にServiceで判定し、想定内の業務エラーとして扱う

---

## Lesson5C 完了条件

- `AttendanceServiceTest` 6件、`UserServiceTest` 3件、`SecurityAccessTest` 3件の合計12件が成功する
- `dev` / `prod` プロファイルでH2コンソール、Thymeleafキャッシュ、初期ユーザー投入の差を説明できる
- 勤怠履歴があるユーザーの削除がServiceで拒否される
- 認証・認可・業務ルール・参照整合性をコード上で追跡できる
- Lesson5全体の完了条件を講師へ説明できる

完了後は [Lesson6 REST API基礎](../lesson06/lesson6.md) へ進みます。
