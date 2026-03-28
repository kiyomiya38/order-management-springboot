import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

class AttendanceController extends BaseController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    AttendanceController(AuthService authService,
                         SessionStore sessionStore,
                         AttendanceService attendanceService,
                         UserService userService) {
        super(authService, sessionStore);
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    void list(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return;
        }
        List<Attendance> attendances = attendanceService.listAttendances(user.getId());
        StringBuilder rows = new StringBuilder();
        if (attendances.isEmpty()) {
            rows.append("<tr><td colspan=\"4\" class=\"muted\">まだ勤怠履歴がありません。</td></tr>");
        } else {
            for (Attendance att : attendances) {
                rows.append("<tr>")
                        .append("<td>").append(att.getWorkDate()).append("</td>")
                        .append("<td>").append(att.getStartTime() != null ? WebUtil.fmt(att.getStartTime()) : "-").append("</td>")
                        .append("<td>").append(att.getEndTime() != null ? WebUtil.fmt(att.getEndTime()) : "-").append("</td>")
                        .append("<td>").append(att.getStatus().getLabel()).append("</td>")
                        .append("</tr>");
            }
        }

        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>勤怠一覧</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>勤怠一覧</h1>
                      <p class="subtitle">%s の履歴（降順）</p>
                      <a href="/">トップへ戻る</a>
                    </header>
                    <section class="panel">
                      <table>
                        <thead>
                          <tr><th>日付</th><th>出勤時刻</th><th>退勤時刻</th><th>状態</th></tr>
                        </thead>
                        <tbody>%s</tbody>
                      </table>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(WebUtil.escapeHtml(userService.getByUsername(user.getUsername()).getUsername()), rows.toString());
        WebUtil.sendHtml(exchange, 200, html);
    }
}
