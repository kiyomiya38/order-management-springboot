# Lesson4: `enum` と業務例外（`BusinessException`）を画面メッセージに接続する

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase1
- このLessonで増える機能: `AttendanceStatus`（enum）と `BusinessException` を導入し、トップ画面のメッセージ表示へ反映する
- 到達点: 不正操作（例: 未出勤で退勤）を業務例外として扱い、画面にエラーメッセージを出せる

## このLessonの方針
- Lesson1〜3の分割構成を維持する
- 変更対象: `Attendance.java`, `DashboardService.java`, `PageHandlers.java`, `HtmlLayout.java`
- 新規作成: `AttendanceStatus.java`, `BusinessException.java`

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson3完了チェック:
- `DashboardService` が存在する
- `PageHandlers` は手動DI構成（`private final DashboardService`）
- `App.java` は `handlers::...` のメソッド参照でルーティングしている

---

## 2. Step1: `AttendanceStatus.java`（enum）を新規作成する

### このStepで学ぶ文法
- `enum` 定義
- enumにフィールドとコンストラクタを持たせる

作成ファイル:
- `AttendanceStatus.java`

```java
public enum AttendanceStatus {
    NOT_CLOCKED_IN("未出勤"),
    WORKING("出勤中"),
    CLOCKED_OUT("退勤済");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
```

---

## 3. Step2: `Attendance.java` を enum 対応へ変更する

### このStepで学ぶ文法
- 文字列状態から型安全な状態管理へ置き換える

変更ファイル:
- `Attendance.java`

変更方法:
- **ファイル全体を置き換える**

```java
public class Attendance {
    private final String workDate;
    private final String clockInTime;
    private final String clockOutTime;
    private final AttendanceStatus status;

    public Attendance(String workDate, String clockInTime, String clockOutTime, AttendanceStatus status) {
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.status = status;
    }

    public String getWorkDate() {
        return workDate;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    // 画面表示用ラベル
    public String getStatusLabel() {
        return status.getLabel();
    }
}
```

---

## 4. Step3: `BusinessException.java` を新規作成する

### このStepで学ぶ文法
- 独自例外クラス
- `RuntimeException` 継承

作成ファイル:
- `BusinessException.java`

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

---

## 5. Step4: `DashboardService.java` に業務ルールを追加する

### このStepで学ぶ文法
- `switch` で状態分岐
- `throw` で業務例外を投げる

変更ファイル:
- `DashboardService.java`

変更方法:
- **ファイル全体を置き換える**

```java
public class DashboardService {
    public User getLoginUser() {
        return new User("user1", "ROLE_USER");
    }

    public Attendance getTodayAttendanceFor(String username) {
        return new Attendance("2026-03-26", "-", "-", AttendanceStatus.NOT_CLOCKED_IN);
    }

    public Attendance getLatestAttendanceFor(String username) {
        return new Attendance("2026-03-26", "-", "-", AttendanceStatus.NOT_CLOCKED_IN);
    }

    // action に応じたメッセージを返す（不正時は BusinessException）
    public String getTopMessageForAction(Attendance attendance, String action) {
        if (action == null || action.isBlank()) {
            return "メッセージ表示エリア（機能は後続Lessonで実装）";
        }

        return switch (action) {
            case "clock-in" -> validateClockIn(attendance);
            case "clock-out" -> validateClockOut(attendance);
            default -> throw new BusinessException("不正な操作です: " + action);
        };
    }

    private String validateClockIn(Attendance attendance) {
        return switch (attendance.getStatus()) {
            case NOT_CLOCKED_IN -> "出勤できます（次Lessonで実処理）";
            case WORKING -> throw new BusinessException("すでに出勤済みです");
            case CLOCKED_OUT -> throw new BusinessException("退勤済みのため再出勤できません");
        };
    }

    private String validateClockOut(Attendance attendance) {
        return switch (attendance.getStatus()) {
            case NOT_CLOCKED_IN -> throw new BusinessException("未出勤のため退勤できません");
            case WORKING -> "退勤できます（次Lessonで実処理）";
            case CLOCKED_OUT -> throw new BusinessException("すでに退勤済みです");
        };
    }
}
```

---

## 6. Step5: `PageHandlers.java` で例外を捕まえて画面へ渡す

### このStepで学ぶ文法
- `try-catch`
- 例外メッセージのUI反映

変更ファイル:
- `PageHandlers.java`

### 6-1. `handleTop` を置き換える
変更場所:
- `public void handleTop(HttpExchange exchange)` メソッド全体
- 差し込みではなく、**メソッド丸ごと置換**する（貼り付けミス防止）

```java
public void handleTop(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    User loginUser = dashboardService.getLoginUser();
    Attendance todayAttendance = dashboardService.getTodayAttendanceFor(loginUser.getUsername());

    String action = readAction(exchange.getRequestURI().getQuery());
    String messageType = "info";
    String message;

    try {
        message = dashboardService.getTopMessageForAction(todayAttendance, action);
    } catch (BusinessException e) {
        message = e.getMessage();
        messageType = "error";
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml(loginUser, todayAttendance, message, messageType));
}
```

### 6-2. `readAction` ヘルパーメソッドを追加
追加場所:
- `handleAdminAttendances` の下（クラス末尾の `}` の前）
- `PageHandlers` クラスの最後が次の形になるように確認する  
  `... handleAdminAttendances() -> readAction() -> クラス終端の }`

```java
private String readAction(String query) {
    if (query == null || query.isBlank()) {
        return "";
    }

    String[] pairs = query.split("&");
    for (String pair : pairs) {
        String[] kv = pair.split("=", 2);
        if (kv.length == 2 && "action".equals(kv[0])) {
            return kv[1];
        }
    }

    return "";
}
```

---

## 7. Step6: `HtmlLayout.java` のトップ画面をメッセージ対応にする

### このStepで学ぶ文法
- メソッド引数追加
- CSSクラスの動的切り替え

変更ファイル:
- `HtmlLayout.java`

### 7-1. `topPageHtml` をメソッド全体で置き換える
変更場所:
- `HtmlLayout.java` の `topPageHtml(...)` メソッド全体
- **部分修正ではなく、下記メソッドで丸ごと置換**する

```java
public static String topPageHtml(User loginUser, Attendance todayAttendance, String message, String messageType) {
    String body = """
            <header>
              <h1>勤怠管理システム（MVP）</h1>
              <p class="subtitle">研修用 / ログインあり</p>
              <div class="row">
                <span class="muted">ログイン中: <strong>%s</strong></span>
                <a href="/attendances">勤怠一覧</a>
                <a href="/users">アカウント管理</a>
                <a href="/admin/attendances">勤怠管理</a>
                <a href="/?action=clock-in">出勤チェック</a>
                <a href="/?action=clock-out">退勤チェック</a>
                <form method="post" action="/logout">
                  <button type="submit" class="danger">ログアウト</button>
                </form>
              </div>
            </header>

            <div class="alert %s">%s</div>

            <section class="panel">
              <div class="panel-header">
                <h2>今日の勤怠</h2>
                <span class="status-badge">%s</span>
              </div>
              <p>日付: %s</p>
              <p>出勤時刻: %s</p>
              <p>退勤時刻: %s</p>
              <div class="row">
                <form method="post" action="/clock-in">
                  <button type="submit">出勤</button>
                </form>
                <form method="post" action="/clock-out">
                  <button type="submit">退勤</button>
                </form>
              </div>
            </section>

            <section class="panel">
              <h2>業務ルール（抜粋）</h2>
              <ul>
                <li>同日に複数回の出勤は不可</li>
                <li>未出勤で退勤は不可</li>
                <li>退勤後に再度退勤は不可</li>
              </ul>
            </section>
            """.formatted(
            loginUser.getUsername(),
            "error".equals(messageType) ? "alert-error" : "alert-info",
            message,
            todayAttendance.getStatusLabel(),
            todayAttendance.getWorkDate(),
            todayAttendance.getClockInTime(),
            todayAttendance.getClockOutTime()
    );

    return wrapHtml("勤怠管理（MVP）", body);
}
```

### 7-2. 確認のみ: `formatted(...)` の引数順（編集不要）
この項目は**ファイル編集ではなく確認だけ**です。  
`topPageHtml` の `%s` は上から次の順番です。
1. `loginUser.getUsername()`
2. `"error".equals(messageType) ? "alert-error" : "alert-info"`
3. `message`
4. `todayAttendance.getStatusLabel()`
5. `todayAttendance.getWorkDate()`
6. `todayAttendance.getClockInTime()`
7. `todayAttendance.getClockOutTime()`

### 7-3. `STYLE_CSS` にエラーメッセージ用クラスを追加
追加場所:
- `.alert-info` の下

```css
.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}
```

### 7-4. このStepの最終チェック
- `HtmlLayout.topPageHtml(...)` の引数が4つ  
  `User, Attendance, String message, String messageType`
- `PageHandlers.handleTop(...)` で `HtmlLayout.topPageHtml(...)` を4引数で呼んでいる

---

## 8. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java DashboardService.java AttendanceStatus.java BusinessException.java
java App
```

ブラウザ確認:
- `http://localhost:8080/`（通常表示）
- `http://localhost:8080/?action=clock-in`（情報メッセージ）
- `http://localhost:8080/?action=clock-out`（エラーメッセージ）

合格条件:
- 未出勤で退勤チェック時に `BusinessException` の文言が画面表示される
- 画面の状態表示は enum のラベル（`AttendanceStatus`）を使っている

---

## 9. Lesson4コードの説明（入力・処理・出力）

1. `AttendanceStatus`（enum）
- 定義元: 自作（`AttendanceStatus`）
- 入力: 列挙値定義（`NOT_CLOCKED_IN` 等）
- 処理: 状態と表示ラベルの対応を1か所に固定
- 出力: 型安全な状態値を扱える

2. `BusinessException`
- 定義元: 自作（`BusinessException`）
- 入力: エラーメッセージ
- 処理: 業務ルール違反を例外として表現
- 出力: 呼び出し側で `catch` して画面表示に使える

3. `getTopMessageForAction(attendance, action)`
- 定義元: 自作（`DashboardService`）
- 入力: 現在勤怠, action文字列
- 処理: actionと状態を照合し、許可/不許可を判定
- 出力: 許可ならメッセージ文字列、不許可なら `BusinessException`

4. `try-catch`（`handleTop`）
- 定義元: Java文法
- 入力: Service呼び出し
- 処理: `BusinessException` を捕まえて `messageType="error"` に切替
- 出力: 画面へ成功/失敗メッセージ表示

---

## 10. よくあるエラー
- `cannot find symbol AttendanceStatus`:
  - `AttendanceStatus.java` の作成漏れ
- `constructor Attendance(...) cannot be applied`:
  - `Attendance` コンストラクタ引数が `String` のままになっていないか確認
- `method topPageHtml(...) cannot be applied`:
  - `HtmlLayout` と `PageHandlers` で引数数が一致しているか確認

## 11. 次Lessonへの引き継ぎ
- Lesson4で業務ルール違反を例外として扱えるようになった
- Lesson5では入力値・一覧・分岐を広げ、Service処理を実用に寄せる
