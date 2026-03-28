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
import java.util.LinkedHashMap;
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

    boolean existsByUsername(String username) {
        return users.containsKey(username);
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

class ValidationResult {
    private final Map<String, ValidationError> errors = new LinkedHashMap<>();

    void addError(String field, String message) {
        if (!errors.containsKey(field)) {
            errors.put(field, new ValidationError(field, message));
        }
    }

    boolean hasErrors() {
        return !errors.isEmpty();
    }

    String getMessage(String field) {
        ValidationError error = errors.get(field);
        return error == null ? "" : error.getMessage();
    }

    Map<String, ValidationError> getAll() {
        return errors;
    }
}

class UserCreateRequest {
    private final String username;
    private final String password;
    private final String displayName;
    private final String role;

    UserCreateRequest(String username, String password, String displayName, String role) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getDisplayName() {
        return displayName;
    }

    String getRole() {
        return role;
    }
}

class UserCreateValidator {
    private final UserRepository userRepository;

    UserCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    ValidationResult validate(UserCreateRequest request) {
        ValidationResult result = new ValidationResult();

        String username = request.getUsername();
        if (username.isBlank()) {
            result.addError("username", "username は必須です。");
        } else {
            if (username.length() > 20) {
                result.addError("username", "username は20文字以内で入力してください。");
            } else if (!username.matches("[a-z0-9_]+")) {
                result.addError("username", "username は半角小文字・数字・_ のみ使用できます。");
            } else if (userRepository.existsByUsername(username)) {
                result.addError("username", "その username は既に使われています。");
            }
        }

        String password = request.getPassword();
        if (password.isBlank()) {
            result.addError("password", "password は必須です。");
        } else if (password.length() < 6) {
            result.addError("password", "password は6文字以上で入力してください。");
        }

        String displayName = request.getDisplayName();
        if (displayName.isBlank()) {
            result.addError("displayName", "displayName は必須です。");
        } else if (displayName.length() > 30) {
            result.addError("displayName", "displayName は30文字以内で入力してください。");
        }

        String role = request.getRole();
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            result.addError("role", "role は USER または ADMIN を選択してください。");
        }

        return result;
    }
}

class UserService {
    private final UserRepository userRepository;
    private final UserCreateValidator validator;

    UserService(UserRepository userRepository, UserCreateValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    ValidationResult createUser(UserCreateRequest request) {
        ValidationResult result = validator.validate(request);
        if (result.hasErrors()) {
            return result;
        }
        userRepository.save(
                new User(
                        request.getUsername(),
                        request.getPassword(),
                        request.getRole(),
                        request.getDisplayName()));
        return result;
    }

    Map<String, User> findAll() {
        return userRepository.findAll();
    }
}

class AuthService {
    private final UserRepository userRepository;
    private final SessionStore sessionStore;

    AuthService(UserRepository userRepository, SessionStore sessionStore) {
        this.userRepository = userRepository;
        this.sessionStore = sessionStore;
    }

    String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        return sessionStore.createSession(username);
    }

    void logout(String sid) {
        if (sid != null) {
            sessionStore.remove(sid);
        }
    }

    User currentUser(HttpExchange exchange) {
        String sid = WebUtil.getCookie(exchange, "sid");
        if (sid == null) {
            return null;
        }
        String username = sessionStore.findUsernameBySid(sid);
        if (username == null) {
            return null;
        }
        return userRepository.findByUsername(username);
    }
}

class WebUtil {
    static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static Map<String, String> parseForm(String body) {
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

    static Map<String, String> parseQuery(String rawQuery) {
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

    static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    static String getCookie(HttpExchange exchange, String name) {
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

    static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    static void sendHtml(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

interface RouteHandler {
    void handle(HttpExchange exchange) throws IOException;
}

class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    void add(String method, String path, RouteHandler handler) {
        routes.put(method + " " + path, handler);
    }

    boolean dispatch(HttpExchange exchange) throws IOException {
        String key = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();
        RouteHandler handler = routes.get(key);
        if (handler == null) {
            return false;
        }
        handler.handle(exchange);
        return true;
    }
}

class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    void getLogin(HttpExchange exchange) throws IOException {
        Map<String, String> query = WebUtil.parseQuery(exchange.getRequestURI().getRawQuery());
        String error = query.getOrDefault("error", "");
        WebUtil.sendHtml(exchange, 200, renderLogin(error));
    }

    void postLogin(HttpExchange exchange) throws IOException {
        Map<String, String> form = WebUtil.parseForm(WebUtil.readBody(exchange));
        String sid = authService.login(form.getOrDefault("username", ""), form.getOrDefault("password", ""));
        if (sid == null) {
            WebUtil.redirect(exchange, "/login?error=" + WebUtil.urlEncode("ログインに失敗しました。"));
            return;
        }
        exchange.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; Path=/; HttpOnly");
        WebUtil.redirect(exchange, "/home");
    }

    void getLogout(HttpExchange exchange) throws IOException {
        String sid = WebUtil.getCookie(exchange, "sid");
        authService.logout(sid);
        exchange.getResponseHeaders().add("Set-Cookie", "sid=; Path=/; Max-Age=0; HttpOnly");
        WebUtil.redirect(exchange, "/login");
    }

    private String renderLogin(String error) {
        return """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Login</title></head>
                <body>
                <h1>ログイン（Lesson5）</h1>
                <p style="color:red">%s</p>
                <form method="post" action="/login">
                  <div>username: <input name="username"></div>
                  <div>password: <input type="password" name="password"></div>
                  <button type="submit">ログイン</button>
                </form>
                <p>admin/adminpass, user/userpass</p>
                </body></html>
                """.formatted(WebUtil.escapeHtml(error));
    }
}

class HomeController {
    private final AuthService authService;

    HomeController(AuthService authService) {
        this.authService = authService;
    }

    void getHome(HttpExchange exchange) throws IOException {
        User current = authService.currentUser(exchange);
        if (current == null) {
            WebUtil.redirect(exchange, "/login");
            return;
        }
        WebUtil.sendHtml(exchange, 200, """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Home</title></head>
                <body>
                <h1>Home</h1>
                <p>username=%s / displayName=%s / role=%s</p>
                <p><a href="/admin/users">管理者: ユーザー一覧</a></p>
                <p><a href="/logout">logout</a></p>
                </body></html>
                """.formatted(
                WebUtil.escapeHtml(current.getUsername()),
                WebUtil.escapeHtml(current.getDisplayName()),
                WebUtil.escapeHtml(current.getRole())));
    }
}

class AdminUserController {
    private final AuthService authService;
    private final UserService userService;

    AdminUserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    void getUserList(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        Map<String, String> query = WebUtil.parseQuery(exchange.getRequestURI().getRawQuery());
        String message = query.getOrDefault("message", "");
        WebUtil.sendHtml(exchange, 200, renderUserList(message));
    }

    void getNewUserForm(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }
        WebUtil.sendHtml(exchange, 200, renderCreateForm("", "", "", "USER", new ValidationResult()));
    }

    void postCreateUser(HttpExchange exchange) throws IOException {
        User admin = requireAdmin(exchange);
        if (admin == null) {
            return;
        }

        Map<String, String> form = WebUtil.parseForm(WebUtil.readBody(exchange));
        UserCreateRequest request = new UserCreateRequest(
                form.getOrDefault("username", ""),
                form.getOrDefault("password", ""),
                form.getOrDefault("displayName", ""),
                form.getOrDefault("role", "USER"));

        ValidationResult result = userService.createUser(request);
        if (result.hasErrors()) {
            WebUtil.sendHtml(
                    exchange,
                    400,
                    renderCreateForm(
                            request.getUsername(),
                            request.getPassword(),
                            request.getDisplayName(),
                            request.getRole(),
                            result));
            return;
        }

        WebUtil.redirect(exchange, "/admin/users?message=" + WebUtil.urlEncode("ユーザーを登録しました。"));
    }

    private User requireAdmin(HttpExchange exchange) throws IOException {
        User current = authService.currentUser(exchange);
        if (current == null) {
            WebUtil.redirect(exchange, "/login");
            return null;
        }
        if (!"ADMIN".equals(current.getRole())) {
            WebUtil.sendText(exchange, 403, "Forbidden");
            return null;
        }
        return current;
    }

    private String renderUserList(String message) {
        StringBuilder li = new StringBuilder();
        for (Map.Entry<String, User> entry : userService.findAll().entrySet()) {
            User user = entry.getValue();
            li.append("<li>")
                    .append(WebUtil.escapeHtml(user.getUsername()))
                    .append(" / ")
                    .append(WebUtil.escapeHtml(user.getDisplayName()))
                    .append(" / ")
                    .append(WebUtil.escapeHtml(user.getRole()))
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
                """.formatted(WebUtil.escapeHtml(message), li.toString());
    }

    private String renderCreateForm(
            String username,
            String password,
            String displayName,
            String role,
            ValidationResult errors) {
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
                WebUtil.escapeHtml(username),
                WebUtil.escapeHtml(errors.getMessage("username")),
                WebUtil.escapeHtml(password),
                WebUtil.escapeHtml(errors.getMessage("password")),
                WebUtil.escapeHtml(displayName),
                WebUtil.escapeHtml(errors.getMessage("displayName")),
                "USER".equals(role) ? "selected" : "",
                "ADMIN".equals(role) ? "selected" : "",
                WebUtil.escapeHtml(errors.getMessage("role")));
    }
}

class AppConfig {
    private final Router router;

    AppConfig() {
        UserRepository userRepository = new UserRepository();
        userRepository.save(new User("admin", "adminpass", "ADMIN", "管理者"));
        userRepository.save(new User("user", "userpass", "USER", "一般ユーザー"));

        SessionStore sessionStore = new SessionStore();
        AuthService authService = new AuthService(userRepository, sessionStore);
        UserCreateValidator validator = new UserCreateValidator(userRepository);
        UserService userService = new UserService(userRepository, validator);

        AuthController authController = new AuthController(authService);
        HomeController homeController = new HomeController(authService);
        AdminUserController adminUserController = new AdminUserController(authService, userService);

        this.router = new Router();
        router.add("GET", "/login", authController::getLogin);
        router.add("POST", "/login", authController::postLogin);
        router.add("GET", "/logout", authController::getLogout);
        router.add("GET", "/home", homeController::getHome);
        router.add("GET", "/admin/users", adminUserController::getUserList);
        router.add("GET", "/admin/users/new", adminUserController::getNewUserForm);
        router.add("POST", "/admin/users/new", adminUserController::postCreateUser);
        router.add("GET", "/health", exchange -> WebUtil.sendText(exchange, 200, "OK"));
    }

    Router router() {
        return router;
    }
}

public class Lesson5Main {
    public static void main(String[] args) throws IOException {
        AppConfig appConfig = new AppConfig();
        Router router = appConfig.router();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            if (!router.dispatch(exchange)) {
                WebUtil.sendText(exchange, 404, "Not Found");
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080/login");
    }
}
