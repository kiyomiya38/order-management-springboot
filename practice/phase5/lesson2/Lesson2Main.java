import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Lesson2Main {
    private static final Map<String, String> PASSWORDS = new HashMap<>();
    private static final Map<String, String> SESSIONS = new HashMap<>();

    static {
        PASSWORDS.put("user", "userpass");
        PASSWORDS.put("admin", "adminpass");
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson2Main::handleRequest);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080/login");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/login".equals(path)) {
            sendHtml(exchange, 200, renderLogin(""));
            return;
        }
        if ("POST".equals(method) && "/login".equals(path)) {
            handleLogin(exchange);
            return;
        }
        if ("GET".equals(method) && "/home".equals(path)) {
            String user = getCurrentUser(exchange);
            if (user == null) {
                redirect(exchange, "/login");
                return;
            }
            sendHtml(exchange, 200, "<h1>Home</h1><p>login user: " + escapeHtml(user) + "</p><p><a href=\"/logout\">logout</a></p>");
            return;
        }
        if ("GET".equals(method) && "/logout".equals(path)) {
            handleLogout(exchange);
            return;
        }
        if ("GET".equals(method) && "/health".equals(path)) {
            sendText(exchange, 200, "OK");
            return;
        }
        sendText(exchange, 404, "Not Found");
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseForm(readBody(exchange));
        String username = form.getOrDefault("username", "");
        String password = form.getOrDefault("password", "");

        String expected = PASSWORDS.get(username);
        if (expected == null || !expected.equals(password)) {
            sendHtml(exchange, 401, renderLogin("ログイン失敗"));
            return;
        }

        String sid = UUID.randomUUID().toString();
        SESSIONS.put(sid, username);
        exchange.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; Path=/; HttpOnly");
        redirect(exchange, "/home");
    }

    private static void handleLogout(HttpExchange exchange) throws IOException {
        String sid = getCookie(exchange, "sid");
        if (sid != null) {
            SESSIONS.remove(sid);
        }
        exchange.getResponseHeaders().add("Set-Cookie", "sid=; Path=/; Max-Age=0; HttpOnly");
        redirect(exchange, "/login");
    }

    private static String getCurrentUser(HttpExchange exchange) {
        String sid = getCookie(exchange, "sid");
        if (sid == null) {
            return null;
        }
        return SESSIONS.get(sid);
    }

    private static String getCookie(HttpExchange exchange, String name) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }
        String[] cookies = cookieHeader.split(";");
        for (String c : cookies) {
            String[] kv = c.trim().split("=", 2);
            if (kv.length == 2 && kv[0].equals(name)) {
                return kv[1];
            }
        }
        return null;
    }

    private static String renderLogin(String error) {
        return """
                <!doctype html>
                <html lang="ja">
                <head><meta charset="UTF-8"><title>Login</title></head>
                <body>
                  <h1>ログイン（Lesson2）</h1>
                  <p style="color:red">%s</p>
                  <form method="post" action="/login">
                    <div>username: <input name="username"></div>
                    <div>password: <input type="password" name="password"></div>
                    <button type="submit">ログイン</button>
                  </form>
                </body>
                </html>
                """.formatted(escapeHtml(error));
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
        String[] pairs = body.split("&");
        for (String pair : pairs) {
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

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
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
