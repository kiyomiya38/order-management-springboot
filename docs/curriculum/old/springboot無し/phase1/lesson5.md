# Lesson5: ミニ課題（Phase1統合: 一覧データと責務分離の仕上げ）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase1
- このLessonで増える機能: `DashboardService` の擬似データ一覧化と、勤怠一覧画面への複数行表示
- 到達点: UIを維持したまま、Controller/Service/Viewの責務分離を説明できる

## このLessonの方針
- Lesson1〜4の分割構成を維持する
- 変更対象: `DashboardService.java`, `PageHandlers.java`, `HtmlLayout.java`
- 新規ファイル追加はなし

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson4完了チェック:
- `AttendanceStatus` と `BusinessException` が存在する
- `/?action=clock-in` と `/?action=clock-out` でメッセージ表示が切り替わる

---

## 2. Step1: `DashboardService.java` を一覧データ対応にする

### このStepで学ぶ文法
- `List<T>`
- コンストラクタ初期化
- 参照用メソッド分離

変更ファイル:
- `DashboardService.java`

変更方法:
- **ファイル全体を置き換える**

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardService {
    // 擬似データ（本来はDBから取得）
    private final List<Attendance> attendanceHistory;

    public DashboardService() {
        List<Attendance> seed = new ArrayList<>();
        seed.add(new Attendance("2026-03-26", "-", "-", AttendanceStatus.NOT_CLOCKED_IN));
        seed.add(new Attendance("2026-03-25", "09:01", "18:12", AttendanceStatus.CLOCKED_OUT));
        seed.add(new Attendance("2026-03-24", "09:07", "17:55", AttendanceStatus.CLOCKED_OUT));
        this.attendanceHistory = Collections.unmodifiableList(seed);
    }

    public User getLoginUser() {
        return new User("user1", "ROLE_USER");
    }

    public Attendance getTodayAttendanceFor(String username) {
        return getLatestAttendanceFor(username);
    }

    public Attendance getLatestAttendanceFor(String username) {
        if (attendanceHistory.isEmpty()) {
            return new Attendance("-", "-", "-", AttendanceStatus.NOT_CLOCKED_IN);
        }
        return attendanceHistory.get(0);
    }

    public List<Attendance> getAttendancesFor(String username) {
        return attendanceHistory;
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

## 3. Step2: `PageHandlers.java` の勤怠一覧処理を一覧取得に変更する

### このStepで学ぶ文法
- Serviceから `List<Attendance>` を受け取る
- Viewへ一覧を渡す

変更ファイル:
- `PageHandlers.java`

変更場所:
- `handleAttendances` メソッド全体

置き換えコード:
```java
public void handleAttendances(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    User loginUser = dashboardService.getLoginUser();
    var rows = dashboardService.getAttendancesFor(loginUser.getUsername());

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.attendancesPageHtml(loginUser, rows));
}
```

---

## 4. Step3: `HtmlLayout.java` の勤怠一覧を複数行表示にする

### このStepで学ぶ文法
- `List<Attendance>`
- `for-each`
- 文字列組み立て（`StringBuilder`）

変更ファイル:
- `HtmlLayout.java`

### 4-1. import追加
ファイル先頭に次を追加:
```java
import java.util.List;
```

### 4-2. `attendancesPageHtml` を置き換える
変更場所:
- `public static String attendancesPageHtml(...)` メソッド全体

置き換えコード:
```java
public static String attendancesPageHtml(User loginUser, List<Attendance> rows) {
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
                  %s
                </tbody>
              </table>
            </section>
            """.formatted(
            loginUser.getUsername(),
            buildAttendanceRows(rows)
    );

    return wrapHtml("勤怠一覧", body);
}
```

### 4-3. `buildAttendanceRows` ヘルパーを追加
追加場所:
- `attendancesPageHtml` の下（`usersPageHtml` の前）

```java
private static String buildAttendanceRows(List<Attendance> rows) {
    if (rows == null || rows.isEmpty()) {
        return """
                <tr>
                  <td colspan="4">データがありません</td>
                </tr>
                """;
    }

    StringBuilder sb = new StringBuilder();
    for (Attendance row : rows) {
        sb.append("""
                <tr>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                </tr>
                """.formatted(
                row.getWorkDate(),
                row.getClockInTime(),
                row.getClockOutTime(),
                row.getStatusLabel()
        ));
    }

    return sb.toString();
}
```

---

## 5. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java DashboardService.java AttendanceStatus.java BusinessException.java
java App
```

ブラウザ確認:
- `http://localhost:8080/attendances` を開く

合格条件:
- 勤怠一覧が複数行で表示される
- 画面表示が崩れない
- `PageHandlers` はデータ作成ロジックを持たず、`DashboardService` 呼び出しだけになっている

---

## 6. ミニ課題（任意）

次のいずれか1つを実装:
1. `DashboardService` の初期データに `WORKING` 状態の行を1件追加する
2. 一覧の先頭に「件数: n件」を表示する
3. `/` のステータスバッジ色を状態ごとに変える（`NOT_CLOCKED_IN` / `WORKING` / `CLOCKED_OUT`）

---

## 7. Lesson5コードの説明（入力・処理・出力）

1. `getAttendancesFor(username)`
- 定義元: 自作（`DashboardService`）
- 入力: `username`
- 処理: 対象ユーザーの擬似履歴一覧を返す
- 出力: `List<Attendance>`

2. `handleAttendances(...)`
- 定義元: 自作（`PageHandlers`）
- 入力: `HttpExchange`
- 処理: Serviceから一覧取得 -> Viewへ渡して描画
- 出力: 勤怠一覧HTMLレスポンス

3. `buildAttendanceRows(rows)`
- 定義元: 自作（`HtmlLayout`）
- 入力: `List<Attendance>`
- 処理: for-each で `<tr>` を連結
- 出力: `<tbody>` 用HTML文字列

---

## 8. よくあるエラー
- `cannot find symbol List`:
  - `HtmlLayout.java` に `import java.util.List;` を追加したか確認
- `method attendancesPageHtml(...) cannot be applied`:
  - `PageHandlers` と `HtmlLayout` の引数型が一致しているか確認
- `cannot find symbol buildAttendanceRows`:
  - ヘルパーメソッド追加漏れ

## 9. 次Lessonへの引き継ぎ
- Phase1の基本骨格（画面・ドメイン・Service・例外）が接続できた
- Lesson6以降で入力/更新系の実処理へ拡張する
