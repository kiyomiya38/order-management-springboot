import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class AdminAttendanceController extends BaseController {
    private final AttendanceService attendanceService;
    private final UserService userService;

    AdminAttendanceController(AuthService authService,
                              SessionStore sessionStore,
                              AttendanceService attendanceService,
                              UserService userService) {
        super(authService, sessionStore);
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    void list(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        FlashData flash = consumeFlash(exchange);
        List<Attendance> attendances = attendanceService.listAllAttendances();

        StringBuilder rows = new StringBuilder();
        if (attendances.isEmpty()) {
            rows.append("<tr><td colspan=\"7\" class=\"muted\">勤怠データがありません。</td></tr>");
        } else {
            for (Attendance att : attendances) {
                rows.append("<tr>")
                        .append("<td>").append(att.getId()).append("</td>")
                        .append("<td>").append(WebUtil.escapeHtml(att.getUser().getUsername())).append("</td>")
                        .append("<td>").append(att.getWorkDate()).append("</td>")
                        .append("<td>").append(att.getStartTime() != null ? WebUtil.fmtHm(att.getStartTime()) : "-").append("</td>")
                        .append("<td>").append(att.getEndTime() != null ? WebUtil.fmtHm(att.getEndTime()) : "-").append("</td>")
                        .append("<td>").append(WebUtil.escapeHtml(att.getStatus().getLabel())).append("</td>")
                        .append("<td><a href=\"/admin/attendances/").append(att.getId()).append("/edit\">編集</a></td>")
                        .append("</tr>");
            }
        }

        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>勤怠管理（管理者）</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>勤怠管理（管理者）</h1>
                      <div class="row">
                        <a href="/">トップへ戻る</a>
                        <a href="/users">アカウント管理</a>
                      </div>
                    </header>
                    %s
                    %s
                    <section class="panel">
                      <table>
                        <thead>
                          <tr>
                            <th>ID</th>
                            <th>ユーザー</th>
                            <th>日付</th>
                            <th>出勤時刻</th>
                            <th>退勤時刻</th>
                            <th>状態</th>
                            <th>操作</th>
                          </tr>
                        </thead>
                        <tbody>%s</tbody>
                      </table>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                flash.getError() == null || flash.getError().isBlank()
                        ? ""
                        : "<div class=\"alert alert-error\">" + WebUtil.escapeHtml(flash.getError()) + "</div>",
                flash.getMessage() == null || flash.getMessage().isBlank()
                        ? ""
                        : "<div class=\"alert alert-info\">" + WebUtil.escapeHtml(flash.getMessage()) + "</div>",
                rows.toString());
        WebUtil.sendHtml(exchange, 200, html);
    }

    void editForm(HttpExchange exchange, Long id) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }

        Attendance attendance = attendanceService.getAttendance(id);
        AdminAttendanceForm form = new AdminAttendanceForm();
        form.setUserId(attendance.getUser().getId());
        form.setUsername(attendance.getUser().getUsername());
        form.setWorkDate(attendance.getWorkDate());
        form.setStartTime(attendance.getStartTime());
        form.setEndTime(attendance.getEndTime());
        form.setStatus(attendance.getStatus());

        renderForm(exchange, id, form, List.of());
    }

    void update(HttpExchange exchange, Long id) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }

        Map<String, String> raw = WebUtil.parseUrlEncoded(WebUtil.readBody(exchange));
        AdminAttendanceForm form = toForm(raw);
        List<String> errors = validate(form, raw);
        if (!errors.isEmpty()) {
            renderForm(exchange, id, form, errors);
            return;
        }

        try {
            attendanceService.updateAttendance(
                    id,
                    form.getUserId(),
                    form.getWorkDate(),
                    form.getStartTime(),
                    form.getEndTime(),
                    form.getStatus()
            );
            putFlash(exchange, "勤怠を更新しました", null);
            WebUtil.redirect(exchange, "/admin/attendances");
        } catch (BusinessException ex) {
            renderForm(exchange, id, form, List.of(ex.getMessage()));
        }
    }

    private AdminAttendanceForm toForm(Map<String, String> raw) {
        AdminAttendanceForm form = new AdminAttendanceForm();

        Long userId = parseLongOrNull(raw.get("userId"));
        form.setUserId(userId);
        if (userId != null) {
            try {
                form.setUsername(userService.get(userId).getUsername());
            } catch (BusinessException ex) {
                form.setUsername("");
            }
        } else {
            form.setUsername("");
        }

        form.setWorkDate(WebUtil.parseDateOrNull(raw.get("workDate")));
        form.setStartTime(WebUtil.parseDateTimeLocalOrNull(raw.get("startTime")));
        form.setEndTime(WebUtil.parseDateTimeLocalOrNull(raw.get("endTime")));
        form.setStatus(parseStatusOrNull(raw.get("status")));
        return form;
    }

    private List<String> validate(AdminAttendanceForm form, Map<String, String> raw) {
        List<String> errors = new ArrayList<>();

        if (form.getUserId() == null) {
            errors.add("must not be null");
        }
        if (form.getWorkDate() == null) {
            errors.add("must not be null");
        }
        if (form.getStatus() == null) {
            errors.add("must not be null");
        }

        String rawStart = raw.getOrDefault("startTime", "");
        if (!rawStart.isBlank() && form.getStartTime() == null) {
            errors.add("must be a valid datetime-local");
        }
        String rawEnd = raw.getOrDefault("endTime", "");
        if (!rawEnd.isBlank() && form.getEndTime() == null) {
            errors.add("must be a valid datetime-local");
        }

        return errors;
    }

    private void renderForm(HttpExchange exchange,
                            Long attendanceId,
                            AdminAttendanceForm form,
                            List<String> errors) throws IOException {
        String notStartedSelected = form.getStatus() == AttendanceStatus.NOT_STARTED ? "selected" : "";
        String workingSelected = form.getStatus() == AttendanceStatus.WORKING ? "selected" : "";
        String finishedSelected = form.getStatus() == AttendanceStatus.FINISHED ? "selected" : "";

        String errorBlock = "";
        if (!errors.isEmpty()) {
            StringBuilder lis = new StringBuilder();
            for (String err : errors) {
                lis.append("<li>").append(WebUtil.escapeHtml(err)).append("</li>");
            }
            errorBlock = "<div class=\"alert alert-error\"><ul>" + lis + "</ul></div>";
        }

        String workDate = form.getWorkDate() == null ? "" : form.getWorkDate().toString();
        String start = WebUtil.fmtDateTimeLocal(form.getStartTime());
        String end = WebUtil.fmtDateTimeLocal(form.getEndTime());

        String html = """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>勤怠編集（管理者）</title>
                  <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                  <div class="container">
                    <header>
                      <h1>勤怠編集（管理者）</h1>
                      <a href="/admin/attendances">一覧へ戻る</a>
                    </header>
                    <section class="panel">
                      <form action="/admin/attendances/%d" method="post">
                        <div class="row">
                          <label>ユーザー
                            <input type="text" value="%s" readonly />
                          </label>
                          <label>日付
                            <input type="date" name="workDate" value="%s" />
                          </label>
                          <label>出勤時刻
                            <input type="datetime-local" name="startTime" value="%s" />
                          </label>
                          <label>退勤時刻
                            <input type="datetime-local" name="endTime" value="%s" />
                          </label>
                          <label>状態
                            <select name="status">
                              <option value="NOT_STARTED" %s>未出勤</option>
                              <option value="WORKING" %s>出勤中</option>
                              <option value="FINISHED" %s>退勤済み</option>
                            </select>
                          </label>
                        </div>
                        <input type="hidden" name="userId" value="%s" />
                        %s
                        <button type="submit">更新</button>
                      </form>
                      <p class="muted">※ 状態と時刻の整合が取れていない場合はエラーになります。</p>
                    </section>
                  </div>
                </body>
                </html>
                """.formatted(
                attendanceId,
                WebUtil.escapeHtml(form.getUsername() == null ? "" : form.getUsername()),
                workDate,
                start,
                end,
                notStartedSelected,
                workingSelected,
                finishedSelected,
                form.getUserId() == null ? "" : String.valueOf(form.getUserId()),
                errorBlock);
        WebUtil.sendHtml(exchange, 200, html);
    }

    private Long parseLongOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private AttendanceStatus parseStatusOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return AttendanceStatus.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
