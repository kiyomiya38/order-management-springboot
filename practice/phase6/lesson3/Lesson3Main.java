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

class UserRepository {
    private final Map<String, String> users = new HashMap<>();

    boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    void save(String username, String displayName) {
        users.put(username, displayName);
    }

    Map<String, String> findAll() {
        return users;
    }
}

class UserValidator {
    private final UserRepository repository;

    UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    Map<String, String> validate(String username, String displayName) {
        Map<String, String> errors = new HashMap<>();
        if (username.isBlank()) {
            errors.put("username", "username は必須です。");
        } else {
            if (username.length() > 20) {
                errors.put("username", "username は20文字以内です。");
            } else if (!username.matches("[a-z0-9_]+")) {
                errors.put("username", "username は小文字英数字と _ のみです。");
            } else if (repository.existsByUsername(username)) {
                errors.put("username", "username は既に使用されています。");
            }
        }

        if (displayName.isBlank()) {
            errors.put("displayName", "displayName は必須です。");
        } else if (displayName.length() < 2 || displayName.length() > 30) {
            errors.put("displayName", "displayName は2〜30文字です。");
        }
        return errors;
    }
}

public class Lesson3Main {
    private static final UserRepository REPOSITORY = new UserRepository();
    private static final UserValidator VALIDATOR = new UserValidator(REPOSITORY);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson3Main::handleRequest);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080/register");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/register".equals(path)) {
            Map<String, String> q = parseQuery(exchange.getRequestURI().getRawQuery());
            sendHtml(exchange, 200, renderForm("", "", "", "", q.getOrDefault("message", "")));
            return;
        }
        if ("POST".equals(method) && "/register".equals(path)) {
            Map<String, String> form = parseForm(readBody(exchange));
            String username = form.getOrDefault("username", "");
            String displayName = form.getOrDefault("displayName", "");
            Map<String, String> errors = VALIDATOR.validate(username, displayName);
            if (!errors.isEmpty()) {
                sendHtml(
                        exchange,
                        400,
                        renderForm(
                                username,
                                displayName,
                                errors.getOrDefault("username", ""),
                                errors.getOrDefault("displayName", ""),
                                ""));
                return;
            }
            REPOSITORY.save(username, displayName);
            redirect(exchange, "/register?message=" + urlEncode("登録しました。"));
            return;
        }
        if ("GET".equals(method) && "/users".equals(path)) {
            StringBuilder html = new StringBuilder("<h1>Users</h1><ul>");
            for (Map.Entry<String, String> e : REPOSITORY.findAll().entrySet()) {
                html.append("<li>").append(escapeHtml(e.getKey())).append(" (").append(escapeHtml(e.getValue())).append(")</li>");
            }
            html.append("</ul><p><a href=\"/register\">register</a></p>");
            sendHtml(exchange, 200, html.toString());
            return;
        }
        sendText(exchange, 404, "Not Found");
    }

    private static String renderForm(String username, String displayName, String usernameError, String displayNameError, String message) {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Register</title></head>
                <body>
                <h1>ユーザー登録（Lesson3）</h1>
                <p style="color:green">%s</p>
                <form method="post" action="/register">
                  <div>username: <input name="username" value="%s"></div>
                  <div style="color:red">%s</div>
                  <div>displayName: <input name="displayName" value="%s"></div>
                  <div style="color:red">%s</div>
                  <button type="submit">登録</button>
                </form>
                <p><a href="/users">一覧</a></p>
                </body></html>
                """.formatted(
                escapeHtml(message),
                escapeHtml(username),
                escapeHtml(usernameError),
                escapeHtml(displayName),
                escapeHtml(displayNameError));
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
