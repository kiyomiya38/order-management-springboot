import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class Lesson2Main {
    private static boolean working = false;
    private static LocalDateTime clockInAt = null;
    private static LocalDateTime clockOutAt = null;
    private static String message = "";
    private static String error = "";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson2Main::handleRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
        System.out.println("Stop: Ctrl + C");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/".equals(path)) {
            sendHtml(exchange, 200, renderHome());
            return;
        }
        if ("GET".equals(method) && "/health".equals(path)) {
            sendText(exchange, 200, "OK");
            return;
        }
        if ("POST".equals(method) && "/clock-in".equals(path)) {
            handleClockIn(exchange);
            return;
        }
        if ("POST".equals(method) && "/clock-out".equals(path)) {
            handleClockOut(exchange);
            return;
        }
        if ("/clock-in".equals(path) || "/clock-out".equals(path)) {
            sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        sendText(exchange, 404, "Not Found");
    }

    private static void handleClockIn(HttpExchange exchange) throws IOException {
        if (working) {
            setError("すでに出勤中です。");
        } else {
            working = true;
            clockInAt = LocalDateTime.now();
            clockOutAt = null;
            setMessage("出勤を記録しました。");
        }
        sendHtml(exchange, 200, renderHome());
    }

    private static void handleClockOut(HttpExchange exchange) throws IOException {
        if (!working) {
            setError("退勤するには先に出勤してください。");
        } else {
            working = false;
            clockOutAt = LocalDateTime.now();
            setMessage("退勤を記録しました。");
        }
        sendHtml(exchange, 200, renderHome());
    }

    private static void setMessage(String value) {
        message = value;
        error = "";
    }

    private static void setError(String value) {
        error = value;
        message = "";
    }

    private static String renderHome() {
        String status = working ? "出勤中" : "未出勤/退勤済み";
        return """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="UTF-8">
                  <title>Phase3 Lesson2</title>
                  <style>
                    body { font-family: sans-serif; margin: 24px; }
                    .ok { color: #0f766e; }
                    .error { color: #b91c1c; }
                    .box { border: 1px solid #ddd; padding: 12px; max-width: 520px; }
                    form { display: inline-block; margin-right: 8px; }
                  </style>
                </head>
                <body>
                  <h1>勤怠打刻（Lesson2）</h1>
                  <p><a href="/health">/health</a></p>
                  <div class="box">
                    <p>現在状態: <strong>%s</strong></p>
                    <p>出勤時刻: %s</p>
                    <p>退勤時刻: %s</p>
                    <p class="ok">%s</p>
                    <p class="error">%s</p>
                    <form method="post" action="/clock-in">
                      <button type="submit">出勤</button>
                    </form>
                    <form method="post" action="/clock-out">
                      <button type="submit">退勤</button>
                    </form>
                  </div>
                </body>
                </html>
                """.formatted(
                status,
                String.valueOf(clockInAt),
                String.valueOf(clockOutAt),
                escapeHtml(message),
                escapeHtml(error));
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void sendHtml(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
