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

public class Lesson4Main {
    private static final Map<String, String> PASSWORDS = new HashMap<>();
    private static final Map<String, String> ROLES = new HashMap<>();
    private static final Map<String, String> SESSIONS = new HashMap<>();
    private static final Map<String, String> ATTENDANCE = new HashMap<>();

    static {
        PASSWORDS.put("user", "userpass");
        PASSWORDS.put("admin", "adminpass");
        ROLES.put("user", "USER");
        ROLES.put("admin", "ADMIN");
        ATTENDANCE.put("user", "未出勤");
        ATTENDANCE.put("admin", "未出勤");
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson4Main::handleRequest);
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
        if ("GET".equals(method) && "/logout".equals(path)) {
            handleLogout(exchange);
            return;
        }
        if ("GET".equals(method) && "/home".equals(path)) {
            String user = requireLogin(exchange);
            if (user == null) {
                return;
            }
            sendHtml(exchange, 200, renderHome(user));
            return;
        }
        if ("POST".equals(method) && "/clock-in".equals(path)) {
            String user = requireLogin(exchange);
            if (user == null) {
                return;
            }
            ATTENDANCE.put(user, "出勤中");
            redirect(exchange, "/home");
            return;
        }
        if ("POST".equals(method) && "/clock-out".equals(path)) {
            String user = requireLogin(exchange);
            if (user == null) {
                return;
            }
            ATTENDANCE.put(user, "退勤済み");
            redirect(exchange, "/home");
            return;
        }
        if ("GET".equals(method) && "/admin/attendances".equals(path)) {
            String user = requireLogin(exchange);
            if (user == null) {
                return;
            }
            if (!"ADMIN".equals(ROLES.getOrDefault(user, "USER"))) {
                sendText(exchange, 403, "Forbidden");
                return;
            }
            sendHtml(exchange, 200, renderAdminAttendances());
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

    private static String requireLogin(HttpExchange exchange) throws IOException {
        String sid = getCookie(exchange, "sid");
        String user = (sid == null) ? null : SESSIONS.get(sid);
        if (user == null) {
            redirect(exchange, "/login");
            return null;
        }
        return user;
    }

    private static String getCookie(HttpExchange exchange, String name) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) {
            return null;
        }
        for (String c : cookieHeader.split(";")) {
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
                <html lang="ja"><head><meta charset="UTF-8"><title>Login</title></head>
                <body>
                <h1>ログイン（Lesson4）</h1>
                <p style="color:red">%s</p>
                <form method="post" action="/login">
                  <div>username: <input name="username"></div>
                  <div>password: <input type="password" name="password"></div>
                  <button type="submit">ログイン</button>
                </form>
                <p>user/userpass, admin/adminpass</p>
                </body></html>
                """.formatted(error);
    }

    private static String renderHome(String user) {
        String role = ROLES.getOrDefault(user, "USER");
        String status = ATTENDANCE.getOrDefault(user, "未出勤");
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Home</title></head>
                <body>
                <h1>Home</h1>
                <p>user=%s role=%s status=%s</p>
                <form method="post" action="/clock-in"><button>出勤</button></form>
                <form method="post" action="/clock-out"><button>退勤</button></form>
                <p><a href="/admin/attendances">勤怠一覧(ADMIN専用)</a></p>
                <p><a href="/logout">logout</a></p>
                </body></html>
                """.formatted(user, role, status);
    }

    private static String renderAdminAttendances() {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Admin</title></head>
                <body>
                <h1>勤怠一覧（ADMIN）</h1>
                <ul>
                  <li>user: %s</li>
                  <li>admin: %s</li>
                </ul>
                <p><a href="/home">home</a></p>
                </body></html>
                """.formatted(ATTENDANCE.get("user"), ATTENDANCE.get("admin"));
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
