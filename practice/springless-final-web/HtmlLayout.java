import java.util.List;

public class HtmlLayout {
  private HtmlLayout() {
  }

  // トップ画面のHTML（固定値表示）
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
        todayAttendance.getClockOutTime());

    return wrapHtml("勤怠管理（MVP）", body);
  }

  public static String loginPageHtml() {
    String body = """
        <header>
          <h1>勤怠管理システム（MVP）</h1>
          <p class="subtitle">ログインしてください</p>
          <a href="/">トップへ戻る</a>
        </header>

        <section class="panel">
          <form method="post" action="/login">
            <div class="row">
              <label>ユーザー名
                <input type="text" name="username" required />
              </label>
              <label>パスワード
                <input type="password" name="password" required />
              </label>
            </div>
            <button type="submit">ログイン</button>
          </form>
          <p class="muted">初期ユーザー: admin / admin123, user1 / password</p>
        </section>
        """;

    return wrapHtml("ログイン", body);
  }

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
        buildAttendanceRows(rows));

    return wrapHtml("勤怠一覧", body);
  }

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
          row.getStatusLabel()));
    }

    return sb.toString();
  }

  public static String usersPageHtml() {
    String body = """
        <header>
          <h1>アカウント管理</h1>
          <div class="row">
            <a href="/">トップへ戻る</a>
            <a href="/users/new">新規作成</a>
          </div>
        </header>

        <section class="panel">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>ユーザー名</th>
                <th>ロール</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>1</td>
                <td>user1</td>
                <td>ROLE_USER</td>
                <td>編集 / 削除</td>
              </tr>
              <tr>
                <td>2</td>
                <td>admin</td>
                <td>ROLE_ADMIN</td>
                <td>編集 / 削除</td>
              </tr>
            </tbody>
          </table>
        </section>
        """;

    return wrapHtml("アカウント管理", body);
  }

  public static String adminAttendancesPageHtml() {
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
              <tr>
                <td>1</td>
                <td>user1</td>
                <td>2026-03-26</td>
                <td>-</td>
                <td>-</td>
                <td>未出勤</td>
                <td>編集</td>
              </tr>
            </tbody>
          </table>
        </section>
        """;

    return wrapHtml("勤怠管理（管理者）", body);
  }

  // 共通のHTML枠
  public static String wrapHtml(String title, String bodyContent) {
    return """
        <!doctype html>
        <html lang="ja">
        <head>
          <meta charset="utf-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1" />
          <title>%s</title>
          <style>%s</style>
        </head>
        <body>
          <div class="container">
            %s
          </div>
        </body>
        </html>
        """.formatted(title, STYLE_CSS, bodyContent);
  }

  // Lesson1用の共通スタイル
  private static final String STYLE_CSS = """
      :root {
        --bg: #f6f6f2;
        --panel: #ffffff;
        --text: #202124;
        --muted: #6b7280;
        --accent: #0ea5e9;
        --border: #e5e7eb;
      }

      * { box-sizing: border-box; }

      body {
        margin: 0;
        font-family: "Segoe UI", Tahoma, sans-serif;
        color: var(--text);
        background: var(--bg);
      }

      .container {
        max-width: 920px;
        margin: 0 auto;
        padding: 24px;
      }

      header { margin-bottom: 16px; }
      h1 { margin: 0 0 4px; }

      .subtitle {
        color: var(--muted);
        margin: 0 0 16px;
      }

      .panel {
        background: var(--panel);
        border: 1px solid var(--border);
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 16px;
      }

      .panel-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      .status-badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 999px;
        background: #e0f2fe;
        color: #0369a1;
        font-size: 12px;
      }

      .row {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        align-items: center;
      }

      input, select {
        padding: 8px;
        border: 1px solid var(--border);
        border-radius: 6px;
      }

      button {
        padding: 8px 12px;
        background: var(--accent);
        color: #fff;
        border: none;
        border-radius: 6px;
        cursor: pointer;
      }

      button:hover { opacity: 0.9; }
      .danger { background: #ef4444; }
      .muted { color: var(--muted); }

      table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
      }

      th, td {
        border-bottom: 1px solid var(--border);
        text-align: left;
        padding: 8px;
      }

      .alert {
        padding: 10px 12px;
        border-radius: 6px;
        margin-bottom: 12px;
      }

      .alert-info {
        background: #e0f2fe;
        color: #075985;
        border: 1px solid #bae6fd;
      }
      .alert-error {
        background: #fee2e2;
        color: #991b1b;
        border: 1px solid #fecaca;
      }
      """;
}
