import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDate;

class HomeController extends BaseController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    HomeController(AuthService authService,
                   SessionStore sessionStore,
                   AttendanceService attendanceService,
                   UserService userService) {
        super(authService, sessionStore);
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    void getIndex(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return;
        }
        FlashData flash = consumeFlash(exchange);
        Attendance today = attendanceService.getTodayAttendance(user.getId());
        AttendanceStatus status = today == null ? AttendanceStatus.NOT_STARTED : today.getStatus();
        String statusClass = switch (status) {
            case WORKING -> "status-badge status-working";
            case FINISHED -> "status-badge status-finished";
            default -> "status-badge";
        };

        String adminLinks = "";
        if ("ROLE_ADMIN".equals(user.getRole())) {
            adminLinks = """
                    <a href="/users">アカウント管理</a>
                    <a href="/admin/attendances">勤怠管理</a>
                    """;
        }

        String clockButtons = "";
        if (status == AttendanceStatus.NOT_STARTED) {
            clockButtons = """
                    <form method="post" action="/clock-in">
                      <button type="submit">出勤</button>
                    </form>
                    """;
        } else if (status == AttendanceStatus.WORKING) {
            clockButtons = """
                    <form method="post" action="/clock-out">
                      <button type="submit">退勤</button>
                    </form>
                    """;
        }

        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>勤怠管理（MVP）</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>勤怠管理システム（MVP）</h1>
                      <p class="subtitle">研修用 / ログインあり</p>
                      <div class="row">
                        <span class="muted">ログイン中: <strong>%s</strong></span>
                        <a href="/attendances">勤怠一覧</a>
                        %s
                        <form method="post" action="/logout">
                          <button type="submit" class="danger">ログアウト</button>
                        </form>
                      </div>
                    </header>
                    %s
                    %s
                    <section class="panel">
                      <div class="panel-header">
                        <h2>今日の勤怠</h2>
                        <span class="%s">%s</span>
                      </div>
                      <p>日付: <span>%s</span></p>
                      <p>出勤時刻: <span>%s</span></p>
                      <p>退勤時刻: <span>%s</span></p>
                      <div class="row">
                        %s
                      </div>
                    </section>
                    <section class="panel">
                      <h2>業務ルール（抜粋）</h2>
                      <ul>
                        <li>同日に複数回の出勤は不可</li>
                        <li>未出勤で退勤は不可</li>
                        <li>退勤後に再度退勤は不可</li>
                      </ul>
                      <p class="muted">※ エラーは画面上部に表示されます。</p>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                WebUtil.escapeHtml(user.getUsername()),
                adminLinks,
                flash.getError() == null || flash.getError().isBlank() ? "" : "<div class=\"alert alert-error\">" + WebUtil.escapeHtml(flash.getError()) + "</div>",
                flash.getMessage() == null || flash.getMessage().isBlank() ? "" : "<div class=\"alert alert-info\">" + WebUtil.escapeHtml(flash.getMessage()) + "</div>",
                statusClass,
                status.getLabel(),
                LocalDate.now(),
                today != null && today.getStartTime() != null ? WebUtil.fmt(today.getStartTime()) : "-",
                today != null && today.getEndTime() != null ? WebUtil.fmt(today.getEndTime()) : "-",
                clockButtons
        );
        WebUtil.sendHtml(exchange, 200, html);
    }

    void postClockIn(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return;
        }
        try {
            attendanceService.clockIn(user.getId());
            putFlash(exchange, "出勤しました", null);
        } catch (BusinessException ex) {
            putFlash(exchange, null, ex.getMessage());
        }
        WebUtil.redirect(exchange, "/");
    }

    void postClockOut(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return;
        }
        try {
            attendanceService.clockOut(user.getId());
            putFlash(exchange, "退勤しました", null);
        } catch (BusinessException ex) {
            putFlash(exchange, null, ex.getMessage());
        }
        WebUtil.redirect(exchange, "/");
    }
}
