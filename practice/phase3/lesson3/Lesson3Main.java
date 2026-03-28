import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Lesson3Main {
    private static boolean working = false;
    private static LocalDateTime clockInAt = null;
    private static LocalDateTime clockOutAt = null;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson3Main::handleRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
        System.out.println("Stop: Ctrl + C");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/".equals(path)) {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            String message = query.getOrDefault("message", "");
            String error = query.getOrDefault("error", "");
            sendHtml(exchange, 200, renderHome(message, error));
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
        if ("GET".equals(method) && "/health".equals(path)) {
            sendText(exchange, 200, "OK");
            return;
        }

        sendText(exchange, 404, "Not Found");
    }

    private static void handleClockIn(HttpExchange exchange) throws IOException {
        if (working) {
            redirectToHome(exchange, "", "すでに出勤中です。");
            return;
        }
        working = true;
        clockInAt = LocalDateTime.now();
        clockOutAt = null;
        redirectToHome(exchange, "出勤を記録しました。", "");
    }

    private static void handleClockOut(HttpExchange exchange) throws IOException {
        if (!working) {
            redirectToHome(exchange, "", "退勤するには先に出勤してください。");
            return;
        }
        working = false;
        clockOutAt = LocalDateTime.now();
        redirectToHome(exchange, "退勤を記録しました。", "");
    }

    private static void redirectToHome(HttpExchange exchange, String message, String error) throws IOException {
        String location = "/?message=" + urlEncode(message) + "&error=" + urlEncode(error);
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private static String renderHome(String message, String error) {
        String status = working ? "出勤中" : "未出勤/退勤済み";
        return """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="UTF-8">
                  <title>Phase3 Lesson3</title>
                  <style>
                    body { font-family: sans-serif; margin: 24px; }
                    .ok { color: #0f766e; }
                    .error { color: #b91c1c; }
                    .box { border: 1px solid #ddd; padding: 12px; max-width: 560px; }
                    form { display: inline-block; margin-right: 8px; }
                  </style>
                </head>
                <body>
                  <h1>勤怠打刻（Lesson3 / PRG）</h1>
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

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return result;
        }
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = urlDecode(kv[0]);
            String value = (kv.length > 1) ? urlDecode(kv[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
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
