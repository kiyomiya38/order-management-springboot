import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson1Main {
    private static final List<String> USERS = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson1Main::handleRequest);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080/register");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/register".equals(path)) {
            sendHtml(exchange, 200, renderForm("", "", ""));
            return;
        }
        if ("POST".equals(method) && "/register".equals(path)) {
            Map<String, String> form = parseForm(readBody(exchange));
            String username = form.getOrDefault("username", "");
            String displayName = form.getOrDefault("displayName", "");

            List<String> errors = new ArrayList<>();
            if (username.isBlank()) {
                errors.add("username は必須です。");
            }
            if (displayName.isBlank()) {
                errors.add("displayName は必須です。");
            }
            if (username.length() > 20) {
                errors.add("username は20文字以内で入力してください。");
            }
            if (displayName.length() > 30) {
                errors.add("displayName は30文字以内で入力してください。");
            }

            if (!errors.isEmpty()) {
                sendHtml(exchange, 400, renderForm(username, displayName, String.join("<br>", errors)));
                return;
            }

            USERS.add(username + " (" + displayName + ")");
            sendHtml(exchange, 200, "<h1>登録成功</h1><p>" + escapeHtml(username) + "</p><p><a href=\"/register\">戻る</a></p>");
            return;
        }
        if ("GET".equals(method) && "/users".equals(path)) {
            StringBuilder html = new StringBuilder("<h1>Users</h1><ul>");
            for (String u : USERS) {
                html.append("<li>").append(escapeHtml(u)).append("</li>");
            }
            html.append("</ul><p><a href=\"/register\">register</a></p>");
            sendHtml(exchange, 200, html.toString());
            return;
        }
        sendText(exchange, 404, "Not Found");
    }

    private static String renderForm(String username, String displayName, String errors) {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Register</title></head>
                <body>
                <h1>ユーザー登録（Lesson1）</h1>
                <p style="color:red">%s</p>
                <form method="post" action="/register">
                  <div>username: <input name="username" value="%s"></div>
                  <div>displayName: <input name="displayName" value="%s"></div>
                  <button type="submit">登録</button>
                </form>
                <p><a href="/users">一覧</a></p>
                </body></html>
                """.formatted(errors, escapeHtml(username), escapeHtml(displayName));
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> result = new HashMap<>();
        if (body == null || body.isEmpty()) {
            return result;
        }
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = (kv.length > 1) ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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
