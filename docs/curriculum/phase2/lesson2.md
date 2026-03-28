# Phase2 Lesson2: 複数ユーザーの当日勤怠一覧を表示する

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase2
- このLessonで増える機能: 複数ユーザーの当日勤怠を `admin/attendances` で動的表示
- 到達点: 1ユーザー向け処理を拡張し、複数ユーザー一覧表示へ接続できる

## このLessonの方針
- Lesson1の分割構成を維持する
- 変更対象: `DashboardService.java`, `AttendanceService.java`, `PageHandlers.java`, `HtmlLayout.java`
- 新規追加: `UserAttendance.java`

---

## 1. 事前準備
```bash
cd ~/order-management-springboot/practice/springless-final-web
```

Lesson1完了チェック:
- `/clock-in` `/clock-out` が動作する
- トップ画面で打刻メッセージが表示される

---

## 2. Step1: `UserAttendance.java` を新規作成する

### このStepで学ぶ文法
- 画面表示用DTO（データをまとめるクラス）
- コンストラクタとgetter

新規作成:
- `UserAttendance.java`

```java
public class UserAttendance {
    private final String username;
    private final Attendance attendance;

    public UserAttendance(String username, Attendance attendance) {
        this.username = username;
        this.attendance = attendance;
    }

    public String getUsername() {
        return username;
    }

    public Attendance getAttendance() {
        return attendance;
    }
}
```

---

## 3. Step2: `DashboardService.java` に複数ユーザー取得を追加する

### このStepで学ぶ文法
- `List.of(...)`
- Serviceに「参照専用メソッド」を追加

変更ファイル:
- `DashboardService.java`

追加場所:
- `getLoginUser()` の下

```java
public java.util.List<User> getAllUsers() {
    return java.util.List.of(
            new User("user1", "ROLE_USER"),
            new User("user2", "ROLE_USER"),
            new User("admin", "ROLE_ADMIN")
    );
}
```

---

## 4. Step3: `AttendanceService.java` に複数ユーザー一覧取得を追加する

### このStepで学ぶ文法
- `for-each` で複数件処理
- `List<UserAttendance>` の返却

変更ファイル:
- `AttendanceService.java`

追加場所:
- `getTodayAttendance(String username)` の下

```java
public java.util.List<UserAttendance> getTodayAttendancesForUsers(java.util.List<User> users) {
    java.util.List<UserAttendance> result = new java.util.ArrayList<>();

    for (User user : users) {
        Attendance today = attendanceRepository.findTodayByUsername(user.getUsername());
        result.add(new UserAttendance(user.getUsername(), today));
    }

    return result;
}
```

---

## 5. Step4: `PageHandlers.java` の管理者勤怠一覧を差し替える

### このStepで学ぶ文法
- Handlerで複数件データを取得
- Viewへ一覧を渡す

変更ファイル:
- `PageHandlers.java`

変更場所:
- `handleAdminAttendances` メソッド全体

置き換えコード:
```java
public void handleAdminAttendances(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    var users = dashboardService.getAllUsers();
    var rows = attendanceService.getTodayAttendancesForUsers(users);

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.adminAttendancesPageHtml(rows));
}
```

---

## 6. Step5: `HtmlLayout.java` の管理者画面を動的表示にする

### このStepで学ぶ文法
- `List<UserAttendance>`
- `StringBuilder` + `for-each`

変更ファイル:
- `HtmlLayout.java`

### 6-1. `adminAttendancesPageHtml` を置き換える
変更場所:
- `public static String adminAttendancesPageHtml(...)` メソッド全体

置き換えコード:
```java
public static String adminAttendancesPageHtml(List<UserAttendance> rows) {
    String body = """
            <header>
              <h1>勤怠管理（管理者）</h1>
              <div class="row">
                <a href="/">トップへ戻る</a>
                <a href="/users">アカウント管理</a>
              </div>
            </header>

            <section class="panel">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>ユーザー名</th>
                    <th>日付</th>
                    <th>出勤時刻</th>
                    <th>退勤時刻</th>
                    <th>状態</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  %s
                </tbody>
              </table>
            </section>
            """.formatted(buildAdminAttendanceRows(rows));

    return wrapHtml("勤怠管理（管理者）", body);
}
```

### 6-2. `buildAdminAttendanceRows` を追加
追加場所:
- `adminAttendancesPageHtml` の下（`wrapHtml` の前）

```java
private static String buildAdminAttendanceRows(List<UserAttendance> rows) {
    if (rows == null || rows.isEmpty()) {
        return """
                <tr>
                  <td colspan="7">データがありません</td>
                </tr>
                """;
    }

    StringBuilder sb = new StringBuilder();
    int id = 1;
    for (UserAttendance row : rows) {
        Attendance a = row.getAttendance();

        sb.append("""
                <tr>
                  <td>%d</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>%s</td>
                  <td>編集</td>
                </tr>
                """.formatted(
                id,
                row.getUsername(),
                a.getWorkDate(),
                a.getClockInTime(),
                a.getClockOutTime(),
                a.getStatusLabel()
        ));
        id++;
    }

    return sb.toString();
}
```

---

## 7. 反映と実行
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java User.java Attendance.java AttendanceStatus.java BusinessException.java AttendanceRepository.java InMemoryAttendanceRepository.java AttendanceService.java DashboardService.java UserAttendance.java
java App
```

ブラウザ確認:
1. `http://localhost:8080/admin/attendances` を開く
2. `user1`, `user2`, `admin` の3行が表示される
3. `/` で `user1` を出勤/退勤後、再度 `/admin/attendances` を開く
4. `user1` 行の状態・時刻が更新される

合格条件:
- 管理者画面が固定1行ではなく、複数ユーザー行を表示できる
- 打刻後の状態が一覧へ反映される

---

## 8. Lesson2コードの説明（入力・処理・出力）

1. `getAllUsers()`
- 定義元: 自作（`DashboardService`）
- 入力: なし
- 処理: 一覧表示対象ユーザーを返す
- 出力: `List<User>`

2. `getTodayAttendancesForUsers(users)`
- 定義元: 自作（`AttendanceService`）
- 入力: `List<User>`
- 処理: 各ユーザーの当日勤怠をRepositoryから取得し、表示DTOへ詰める
- 出力: `List<UserAttendance>`

3. `buildAdminAttendanceRows(rows)`
- 定義元: 自作（`HtmlLayout`）
- 入力: `List<UserAttendance>`
- 処理: `for-each` で `<tr>` を組み立てる
- 出力: `<tbody>` に埋め込むHTML文字列

---

## 9. よくあるエラー
- `method adminAttendancesPageHtml in class HtmlLayout cannot be applied`:
  - `PageHandlers` 側が `rows` を渡しているか確認
- `cannot find symbol UserAttendance`:
  - `UserAttendance.java` の作成漏れ
- `cannot find symbol List`:
  - `HtmlLayout.java` 先頭に `import java.util.List;` があるか確認

## 10. 次Lessonへの引き継ぎ
- Lesson2で複数ユーザー一覧表示が完成
- Lesson3でRepository境界をさらに明確化し、差し替え可能性を高める
