import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PageHandlers {
    // 依存先を private final で保持（作成後に差し替え不可）
    private final DashboardService dashboardService;
    private final AttendanceService attendanceService;

    public PageHandlers(DashboardService dashboardService, AttendanceService attendanceService) {
        this.dashboardService = dashboardService;
        this.attendanceService = attendanceService;
    }

    // トップ画面（/）
    public void handleTop(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        User loginUser = dashboardService.getLoginUser();
        Attendance todayAttendance = attendanceService.getTodayAttendance(loginUser.getUsername());

        String message = readQueryParam(exchange, "msg");
        String messageType = readQueryParam(exchange, "type");

        if (message == null || message.isBlank()) {
            message = "メッセージ表示エリア（機能は後続Lessonで実装）";
        }
        if (messageType == null || messageType.isBlank()) {
            messageType = "info";
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml(loginUser, todayAttendance, message, messageType));
    }

    public void handleClockIn(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        String username = dashboardService.getLoginUser().getUsername();

        try {
            attendanceService.clockIn(username);
            redirectWithMessage(exchange, "info", "出勤しました");
        } catch (BusinessException e) {
            redirectWithMessage(exchange, "error", e.getMessage());
        }
    }

    public void handleClockOut(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        String username = dashboardService.getLoginUser().getUsername();

        try {
            attendanceService.clockOut(username);
            redirectWithMessage(exchange, "info", "退勤しました");
        } catch (BusinessException e) {
            redirectWithMessage(exchange, "error", e.getMessage());
        }
    }

    // 監視確認（/health）
    public void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendText(exchange, 200, "OK");
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.loginPageHtml());
    }

    public void handleAttendances(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        User loginUser = dashboardService.getLoginUser();
        var rows = dashboardService.getAttendancesFor(loginUser.getUsername());

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.attendancesPageHtml(loginUser, rows));
    }

    public void handleUsers(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.usersPageHtml());
    }

    public void handleAdminAttendances(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.adminAttendancesPageHtml());
    }

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

    private void redirectWithMessage(HttpExchange exchange, String type, String msg) throws IOException {
        String encodedType = java.net.URLEncoder.encode(type, java.nio.charset.StandardCharsets.UTF_8);
        String encodedMsg = java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8);
        HttpResponses.sendRedirect(exchange, "/?type=" + encodedType + "&msg=" + encodedMsg);
    }

    private String readQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isBlank()) {
            return "";
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && key.equals(kv[0])) {
                return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
            }
        }

        return "";
    }
}