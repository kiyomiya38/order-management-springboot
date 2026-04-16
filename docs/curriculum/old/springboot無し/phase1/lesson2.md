# Lesson2: コンストラクタ・引数・this・private（分割構成のまま固定値をオブジェクト化）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase1
- このLessonで増える機能: `User` / `Attendance` クラスを追加し、トップ画面と勤怠一覧の固定値をオブジェクト値で表示する
- 到達点: 画面は同じ見た目のまま、表示値の取得元を「文字直書き」から「クラスの値」に変更できる

## このLessonの方針
- Lesson1の分割構成を維持する
- 対象ファイル: `App.java`, `PageHandlers.java`, `HtmlLayout.java`, `HttpResponses.java` + 新規 `User.java`, `Attendance.java`
- DB・認証本処理はまだ実装しない

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson1完了チェック:
- `http://localhost:8080/`
- `http://localhost:8080/login`
- `http://localhost:8080/attendances`
- `http://localhost:8080/users`

---

## 2. Step1: `User.java` を新規作成する

### このStepで学ぶ文法
- クラス定義
- `private final` フィールド
- コンストラクタ
- getter

作成ファイル:
- `User.java`

```java
public class User {
    // 外部から直接変更させない
    private final String username;
    private final String role;

    // 引数を受け取り、thisでフィールドへ保存する
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
```

---

## 3. Step2: `Attendance.java` を新規作成する

### このStepで学ぶ文法
- 複数フィールドを持つクラス設計
- コンストラクタでの初期化

作成ファイル:
- `Attendance.java`

```java
public class Attendance {
    private final String workDate;
    private final String clockInTime;
    private final String clockOutTime;
    private final String statusLabel;

    public Attendance(String workDate, String clockInTime, String clockOutTime, String statusLabel) {
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.statusLabel = statusLabel;
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

    public String getStatusLabel() {
        return statusLabel;
    }
}
```

---

## 4. Step3: `HtmlLayout.java` の表示メソッドを引数対応にする

### このStepで学ぶ文法
- メソッド引数（`User`, `Attendance`）
- getter経由で値取得
- `formatted(...)` で埋め込み

変更ファイル:
- `HtmlLayout.java`

### 4-1. `topPageHtml()` を置き換える
変更場所:
- `public static String topPageHtml()` のメソッド全体

置き換えコード:
```java
public static String topPageHtml(User loginUser, Attendance todayAttendance) {
    String body = """
            <header>
              <h1>勤怠管理システム（MVP）</h1>
              <p class="subtitle">研修用 / ログインあり</p>
              <div class="row">
                <span class="muted">ログイン中: <strong>%s</strong></span>
                <a href="/attendances">勤怠一覧</a>
                <a href="/users">アカウント管理</a>
                <a href="/admin/attendances">勤怠管理</a>
                <form method="post" action="/logout">
                  <button type="submit" class="danger">ログアウト</button>
                </form>
              </div>
            </header>

            <div class="alert alert-info">メッセージ表示エリア（機能は後続Lessonで実装）</div>

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
            todayAttendance.getStatusLabel(),
            todayAttendance.getWorkDate(),
            todayAttendance.getClockInTime(),
            todayAttendance.getClockOutTime()
    );

    return wrapHtml("勤怠管理（MVP）", body);
}
```

### 4-2. `attendancesPageHtml()` を置き換える
変更場所:
- `public static String attendancesPageHtml()` のメソッド全体

置き換えコード:
```java
public static String attendancesPageHtml(User loginUser, Attendance row) {
    String body = """
            <header>
              <h1>勤怠一覧</h1>
              <p class="subtitle">%s の履歴（降順）</p>
              <a href="/">トップへ戻る</a>
            </header>

            <section class="panel">
              <table>
                <thead>
                  <tr>
                    <th>日付</th>
                    <th>出勤時刻</th>
                    <th>退勤時刻</th>
                    <th>状態</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                  </tr>
                </tbody>
              </table>
            </section>
            """.formatted(
            loginUser.getUsername(),
            row.getWorkDate(),
            row.getClockInTime(),
            row.getClockOutTime(),
            row.getStatusLabel()
    );

    return wrapHtml("勤怠一覧", body);
}
```

---

## 5. Step4: `PageHandlers.java` でオブジェクトを作って画面へ渡す

### このStepで学ぶ文法
- `new` によるインスタンス生成
- 引数ありメソッド呼び出し

変更ファイル:
- `PageHandlers.java`

### 5-1. `handleTop` を置き換える
変更場所:
- `public static void handleTop(HttpExchange exchange)` メソッド全体

置き換えコード:
```java
public static void handleTop(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    User loginUser = new User("user1", "ROLE_USER");
    Attendance todayAttendance = new Attendance("2026-03-26", "-", "-", "未出勤");

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml(loginUser, todayAttendance));
}
```

### 5-2. `handleAttendances` を置き換える
変更場所:
- `public static void handleAttendances(HttpExchange exchange)` メソッド全体

置き換えコード:
```java
public static void handleAttendances(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    User loginUser = new User("user1", "ROLE_USER");
    Attendance row = new Attendance("2026-03-26", "-", "-", "未出勤");

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.attendancesPageHtml(loginUser, row));
}
```

---

## 6. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java
java App
```

ブラウザ確認:
- `http://localhost:8080/`
- `http://localhost:8080/attendances`

確認ポイント:
- 見た目は同じでも、`user1` / 日付 / 状態がオブジェクト由来になっている

---

## 7. Lesson2コードの説明（入力・処理・出力）

1. `User(String username, String role)`
- 定義元: 自作（`User`）
- 入力: `username`, `role`
- 処理: `this.username = username; this.role = role;`
- 出力: 値を保持した `User` が使える状態になる

2. `Attendance(String workDate, String clockInTime, String clockOutTime, String statusLabel)`
- 定義元: 自作（`Attendance`）
- 入力: 日付・出勤時刻・退勤時刻・状態
- 処理: 各フィールドへ初期値を保存
- 出力: 値を保持した `Attendance` が使える状態になる

3. `this` の意味
- 左側の `this.xxx` は「オブジェクト自身のフィールド」
- 右側の `xxx` は「引数」
- `this` で対象を明確化できる

4. `private` の意味
- 外部からの直接変更を防ぐ
- 値読み取りは getter 経由に統一する

---

## 8. よくあるエラー
- `method topPageHtml in class HtmlLayout cannot be applied to given types`:
  - `HtmlLayout` 側と `PageHandlers` 側の引数定義が揃っているか確認
- `cannot find symbol User / Attendance`:
  - `User.java` / `Attendance.java` の作成漏れ
- `cannot find symbol getStatusLabel`:
  - getter名の誤字確認

## 9. 次Lessonへの引き継ぎ
- Lesson2で「固定値 -> オブジェクト値」の変換が完了
- Lesson3では一覧件数や状態分岐など、処理の中身を拡張していく
