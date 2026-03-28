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

class User {
    private final String username;
    private final String password;
    private final String role;

    User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
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
}

class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    void save(User user) {
        users.put(user.getUsername(), user);
    }

    User findByUsername(String username) {
        return users.get(username);
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

class AttendanceService {
    private final Map<String, String> attendance = new HashMap<>();

    void ensureUser(String username) {
        attendance.putIfAbsent(username, "未出勤");
    }

    void clockIn(String username) {
        attendance.put(username, "出勤中");
    }

    void clockOut(String username) {
        attendance.put(username, "退勤済み");
    }

    String getStatus(String username) {
        return attendance.getOrDefault(username, "未出勤");
    }

    Map<String, String> getAll() {
        return attendance;
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
        WebUtil.sendHtml(exchange, 200, """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Login</title></head>
                <body>
                <h1>ログイン（Lesson5）</h1>
                <form method="post" action="/login">
                  <div>username: <input name="username"></div>
                  <div>password: <input type="password" name="password"></div>
                  <button type="submit">ログイン</button>
                </form>
                <p>user/userpass, admin/adminpass</p>
                </body></html>
                """);
    }

    void postLogin(HttpExchange exchange) throws IOException {
        Map<String, String> form = WebUtil.parseForm(WebUtil.readBody(exchange));
        String sid = authService.login(form.getOrDefault("username", ""), form.getOrDefault("password", ""));
        if (sid == null) {
            WebUtil.sendText(exchange, 401, "Login failed");
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
}

class HomeController {
    private final AuthService authService;
    private final AttendanceService attendanceService;

    HomeController(AuthService authService, AttendanceService attendanceService) {
        this.authService = authService;
        this.attendanceService = attendanceService;
    }

    void getHome(HttpExchange exchange) throws IOException {
        User user = authService.currentUser(exchange);
        if (user == null) {
            WebUtil.redirect(exchange, "/login");
            return;
        }
        attendanceService.ensureUser(user.getUsername());
        String status = attendanceService.getStatus(user.getUsername());
        WebUtil.sendHtml(exchange, 200, """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Home</title></head>
                <body>
                <h1>Home</h1>
                <p>user=%s role=%s status=%s</p>
                <form method="post" action="/clock-in"><button>出勤</button></form>
                <form method="post" action="/clock-out"><button>退勤</button></form>
                <p><a href="/admin/attendances">勤怠一覧(ADMIN)</a></p>
                <p><a href="/logout">logout</a></p>
                </body></html>
                """.formatted(user.getUsername(), user.getRole(), status));
    }

    void postClockIn(HttpExchange exchange) throws IOException {
        User user = authService.currentUser(exchange);
        if (user == null) {
            WebUtil.redirect(exchange, "/login");
            return;
        }
        attendanceService.clockIn(user.getUsername());
        WebUtil.redirect(exchange, "/home");
    }

    void postClockOut(HttpExchange exchange) throws IOException {
        User user = authService.currentUser(exchange);
        if (user == null) {
            WebUtil.redirect(exchange, "/login");
            return;
        }
        attendanceService.clockOut(user.getUsername());
        WebUtil.redirect(exchange, "/home");
    }
}

class AdminController {
    private final AuthService authService;
    private final AttendanceService attendanceService;

    AdminController(AuthService authService, AttendanceService attendanceService) {
        this.authService = authService;
        this.attendanceService = attendanceService;
    }

    void getAttendances(HttpExchange exchange) throws IOException {
        User user = authService.currentUser(exchange);
        if (user == null) {
            WebUtil.redirect(exchange, "/login");
            return;
        }
        if (!"ADMIN".equals(user.getRole())) {
            WebUtil.sendText(exchange, 403, "Forbidden");
            return;
        }
        StringBuilder lines = new StringBuilder();
        for (Map.Entry<String, String> e : attendanceService.getAll().entrySet()) {
            lines.append("<li>").append(e.getKey()).append(": ").append(e.getValue()).append("</li>");
        }
        WebUtil.sendHtml(exchange, 200, """
                <!doctype html>
                <html lang="ja"><head><meta charset="UTF-8"><title>Admin</title></head>
                <body>
                <h1>勤怠一覧（ADMIN）</h1>
                <ul>%s</ul>
                <p><a href="/home">home</a></p>
                </body></html>
                """.formatted(lines.toString()));
    }
}

class AppConfig {
    private final Router router;

    AppConfig() {
        UserRepository userRepository = new UserRepository();
        userRepository.save(new User("user", "userpass", "USER"));
        userRepository.save(new User("admin", "adminpass", "ADMIN"));

        SessionStore sessionStore = new SessionStore();
        AuthService authService = new AuthService(userRepository, sessionStore);
        AttendanceService attendanceService = new AttendanceService();

        AuthController authController = new AuthController(authService);
        HomeController homeController = new HomeController(authService, attendanceService);
        AdminController adminController = new AdminController(authService, attendanceService);

        this.router = new Router();
        router.add("GET", "/login", authController::getLogin);
        router.add("POST", "/login", authController::postLogin);
        router.add("GET", "/logout", authController::getLogout);
        router.add("GET", "/home", homeController::getHome);
        router.add("POST", "/clock-in", homeController::postClockIn);
        router.add("POST", "/clock-out", homeController::postClockOut);
        router.add("GET", "/admin/attendances", adminController::getAttendances);
        router.add("GET", "/health", ex -> WebUtil.sendText(ex, 200, "OK"));
    }

    Router router() {
        return router;
    }
}

public class Lesson5Main {
    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();
        Router router = config.router();

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
