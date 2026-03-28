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

class ValidationError {
    private final String field;
    private final String message;

    ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    String getField() {
        return field;
    }

    String getMessage() {
        return message;
    }
}

class UserFormValidator {
    List<ValidationError> validate(String username, String displayName) {
        List<ValidationError> errors = new ArrayList<>();
        if (username.isBlank()) {
            errors.add(new ValidationError("username", "username は必須です。"));
        } else {
            if (username.length() > 20) {
                errors.add(new ValidationError("username", "username は20文字以内です。"));
            }
            if (!username.matches("[a-z0-9_]+")) {
                errors.add(new ValidationError("username", "username は小文字英数字と _ のみです。"));
            }
        }
        if (displayName.isBlank()) {
            errors.add(new ValidationError("displayName", "displayName は必須です。"));
        } else {
            if (displayName.length() < 2) {
                errors.add(new ValidationError("displayName", "displayName は2文字以上です。"));
            }
            if (displayName.length() > 30) {
                errors.add(new ValidationError("displayName", "displayName は30文字以内です。"));
            }
        }
        return errors;
    }
}

public class Lesson2Main {
    private static final List<String> USERS = new ArrayList<>();
    private static final UserFormValidator VALIDATOR = new UserFormValidator();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", Lesson2Main::handleRequest);
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080/register");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/register".equals(path)) {
            sendHtml(exchange, 200, renderForm("", "", "", ""));
            return;
        }
        if ("POST".equals(method) && "/register".equals(path)) {
            Map<String, String> form = parseForm(readBody(exchange));
            String username = form.getOrDefault("username", "");
            String displayName = form.getOrDefault("displayName", "");

            List<ValidationError> errors = VALIDATOR.validate(username, displayName);
            if (!errors.isEmpty()) {
                String usernameError = firstError(errors, "username");
                String displayNameError = firstError(errors, "displayName");
                sendHtml(exchange, 400, renderForm(username, displayName, usernameError, displayNameError));
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

    private static String firstError(List<ValidationError> errors, String field) {
        for (ValidationError e : errors) {
            if (e.getField().equals(field)) {
                return e.getMessage();
            }
        }
        return "";
    }

    private static String renderForm(String username, String displayName, String usernameError, String displayNameError) {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Register</title></head>
                <body>
                <h1>ユーザー登録（Lesson2）</h1>
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
