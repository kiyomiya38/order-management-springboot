import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class User {
    private final String username;
    private final String password;
    private final String role;
    private final String displayName;

    User(String username, String password, String role, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getRole() {
        return role;
    }

    String getDisplayName() {
        return displayName;
    }
}

class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    void save(User user) {
        users.put(user.getUsername(), user);
    }

    User findByUsername(String username) {
        return users.get(username);
    }

    Map<String, User> findAll() {
        return users;
    }
}

class SessionStore {
    private final Map<String, String> sessions = new HashMap<>();

    String createSession(String username) {
        String sid = UUID.randomUUID().toString();
        sessions.put(sid, username);
        return sid;
    }

    String findUsernameBySid(String sid) {
        return sessions.get(sid);
    }

    void remove(String sid) {
        sessions.remove(sid);
    }
}

class UserCreateValidator {
    private final UserRepository repository;

    UserCreateValidator(UserRepository repository) {
        this.repository = repository;
    }

    Map<String, String> validate(String username, String password, String displayName, String role) {
        Map<String, String> errors = new HashMap<>();
        if (username.isBlank()) {
            errors.put("username", "username は必須です。");
        } else {
            if (!username.matches("[a-z0-9_]+")) {
                errors.put("username", "username は小文字英数字と _ のみです。");
            } else if (repository.findByUsername(username) != null) {
                errors.put("username", "username は既に存在します。");
            }
        }

        if (password.isBlank()) {
            errors.put("password", "password は必須です。");
        } else if (password.length() < 6) {
            errors.put("password", "password は6文字以上です。");
        }

        if (displayName.isBlank()) {
            errors.put("displayName", "displayName は必須です。");
        } else if (displayName.length() > 30) {
            errors.put("displayName", "displayName は30文字以内です。");
        }

        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            errors.put("role", "role は USER または ADMIN です。");
        }
        return errors;
    }
}

public class Lesson4Main {
    private static final UserRepository USER_REPOSITORY = new UserRepository();
    private static final SessionStore SESSION_STORE = new SessionStore();
    private static final UserCreateValidator VALIDATOR = new UserCreateValidator(USER_REPOSITORY);

    public static void main(String[] args) throws IOException {
        USER_REPOSITORY.save(new User("admin", "adminpass", "ADMIN", "管理者"));
        USER_REPOSITORY.save(new User("user", "userpass", "USER", "一般ユーザー"));

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
            User me = requireLogin(exchange);
            if (me == null) {
                return;
            }
            sendHtml(exchange, 200, "<h1>Home</h1><p>" + me.getUsername() + " / " + me.getRole() + "</p><p><a href=\"/admin/users\">admin users</a></p><p><a href=\"/logout\">logout</a></p>");
            return;
        }
        if ("GET".equals(method) && "/admin/users".equals(path)) {
            User me = requireAdmin(exchange);
            if (me == null) {
                return;
            }
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            String message = query.getOrDefault("message", "");
            sendHtml(exchange, 200, renderUserList(message));
            return;
        }
        if ("GET".equals(method) && "/admin/users/new".equals(path)) {
            User me = requireAdmin(exchange);
            if (me == null) {
                return;
            }
            sendHtml(exchange, 200, renderCreateForm("", "", "", "USER", new HashMap<>()));
            return;
        }
        if ("POST".equals(method) && "/admin/users/new".equals(path)) {
            User me = requireAdmin(exchange);
            if (me == null) {
                return;
            }
            handleCreateUser(exchange);
            return;
        }

        sendText(exchange, 404, "Not Found");
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseForm(readBody(exchange));
        String username = form.getOrDefault("username", "");
        String password = form.getOrDefault("password", "");
        User user = USER_REPOSITORY.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            sendHtml(exchange, 401, renderLogin("ログイン失敗"));
            return;
        }
        String sid = SESSION_STORE.createSession(user.getUsername());
        exchange.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; Path=/; HttpOnly");
        redirect(exchange, "/home");
    }

    private static void handleLogout(HttpExchange exchange) throws IOException {
        String sid = getCookie(exchange, "sid");
        if (sid != null) {
            SESSION_STORE.remove(sid);
        }
        exchange.getResponseHeaders().add("Set-Cookie", "sid=; Path=/; Max-Age=0; HttpOnly");
        redirect(exchange, "/login");
    }

    private static void handleCreateUser(HttpExchange exchange) throws IOException {
        Map<String, String> form = parseForm(readBody(exchange));
        String username = form.getOrDefault("username", "");
        String password = form.getOrDefault("password", "");
        String displayName = form.getOrDefault("displayName", "");
        String role = form.getOrDefault("role", "USER");

        Map<String, String> errors = VALIDATOR.validate(username, password, displayName, role);
        if (!errors.isEmpty()) {
            sendHtml(exchange, 400, renderCreateForm(username, password, displayName, role, errors));
            return;
        }

        USER_REPOSITORY.save(new User(username, password, role, displayName));
        redirect(exchange, "/admin/users?message=" + urlEncode("ユーザーを登録しました。"));
    }

    private static User requireLogin(HttpExchange exchange) throws IOException {
        String sid = getCookie(exchange, "sid");
        String username = (sid == null) ? null : SESSION_STORE.findUsernameBySid(sid);
        if (username == null) {
            redirect(exchange, "/login");
            return null;
        }
        return USER_REPOSITORY.findByUsername(username);
    }

    private static User requireAdmin(HttpExchange exchange) throws IOException {
        User user = requireLogin(exchange);
        if (user == null) {
            return null;
        }
        if (!"ADMIN".equals(user.getRole())) {
            sendText(exchange, 403, "Forbidden");
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
                <p>admin/adminpass, user/userpass</p>
                </body></html>
                """.formatted(escapeHtml(error));
    }

    private static String renderUserList(String message) {
        StringBuilder li = new StringBuilder();
        for (Map.Entry<String, User> e : USER_REPOSITORY.findAll().entrySet()) {
            User u = e.getValue();
            li.append("<li>")
                    .append(escapeHtml(u.getUsername()))
                    .append(" / ")
                    .append(escapeHtml(u.getDisplayName()))
                    .append(" / ")
                    .append(escapeHtml(u.getRole()))
                    .append("</li>");
        }
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Users</title></head>
                <body>
                <h1>ユーザー一覧（ADMIN）</h1>
                <p style="color:green">%s</p>
                <ul>%s</ul>
                <p><a href="/admin/users/new">新規作成</a></p>
                <p><a href="/home">home</a></p>
                </body></html>
                """.formatted(escapeHtml(message), li.toString());
    }

    private static String renderCreateForm(
            String username,
            String password,
            String displayName,
            String role,
            Map<String, String> errors) {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Create User</title></head>
                <body>
                <h1>ユーザー新規作成（ADMIN）</h1>
                <form method="post" action="/admin/users/new">
                  <div>username: <input name="username" value="%s"></div>
                  <div style="color:red">%s</div>
                  <div>password: <input type="password" name="password" value="%s"></div>
                  <div style="color:red">%s</div>
                  <div>displayName: <input name="displayName" value="%s"></div>
                  <div style="color:red">%s</div>
                  <div>role:
                    <select name="role">
                      <option value="USER" %s>USER</option>
                      <option value="ADMIN" %s>ADMIN</option>
                    </select>
                  </div>
                  <div style="color:red">%s</div>
                  <button type="submit">登録</button>
                </form>
                <p><a href="/admin/users">一覧へ戻る</a></p>
                </body></html>
                """.formatted(
                escapeHtml(username),
                escapeHtml(errors.getOrDefault("username", "")),
                escapeHtml(password),
                escapeHtml(errors.getOrDefault("password", "")),
                escapeHtml(displayName),
                escapeHtml(errors.getOrDefault("displayName", "")),
                "USER".equals(role) ? "selected" : "",
                "ADMIN".equals(role) ? "selected" : "",
                escapeHtml(errors.getOrDefault("role", "")));
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

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return result;
        }
        for (String pair : rawQuery.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = (kv.length > 1) ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
