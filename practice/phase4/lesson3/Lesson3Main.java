import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

enum AttendanceStatus {
    NOT_WORKING("未出勤"),
    WORKING("出勤中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }
}

class User {
    private final Long id;
    private final String name;

    User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    Long getId() {
        return id;
    }

    String getName() {
        return name;
    }
}

class Attendance {
    private final Long userId;
    private final LocalDate date;
    private AttendanceStatus status = AttendanceStatus.NOT_WORKING;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;

    Attendance(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    Long getUserId() {
        return userId;
    }

    LocalDate getDate() {
        return date;
    }

    AttendanceStatus getStatus() {
        return status;
    }

    LocalDateTime getClockInAt() {
        return clockInAt;
    }

    LocalDateTime getClockOutAt() {
        return clockOutAt;
    }

    void clockIn(LocalDateTime now) {
        if (status != AttendanceStatus.NOT_WORKING) {
            throw new BusinessException("すでに出勤済みです。");
        }
        status = AttendanceStatus.WORKING;
        clockInAt = now;
    }

    void clockOut(LocalDateTime now) {
        if (status != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください。");
        }
        status = AttendanceStatus.FINISHED;
        clockOutAt = now;
    }
}

interface UserRepository {
    void save(User user);

    User findById(Long id);
}

class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public User findById(Long id) {
        return users.get(id);
    }
}

interface AttendanceRepository {
    Attendance findOrCreate(Long userId, LocalDate date);

    List<Attendance> findAllByDate(LocalDate date);
}

class InMemoryAttendanceRepository implements AttendanceRepository {
    private final Map<String, Attendance> attendances = new HashMap<>();

    public Attendance findOrCreate(Long userId, LocalDate date) {
        String key = userId + "_" + date;
        if (!attendances.containsKey(key)) {
            attendances.put(key, new Attendance(userId, date));
        }
        return attendances.get(key);
    }

    public List<Attendance> findAllByDate(LocalDate date) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance attendance : attendances.values()) {
            if (attendance.getDate().equals(date)) {
                result.add(attendance);
            }
        }
        return result;
    }
}

class AttendanceService {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    AttendanceService(UserRepository userRepository, AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
    }

    Attendance getTodayAttendance(Long userId) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessException("ユーザーが存在しません。");
        }
        return attendanceRepository.findOrCreate(userId, LocalDate.now());
    }

    void clockIn(Long userId) {
        getTodayAttendance(userId).clockIn(LocalDateTime.now());
    }

    void clockOut(Long userId) {
        getTodayAttendance(userId).clockOut(LocalDateTime.now());
    }

    List<Attendance> getDailyAttendances(LocalDate date) {
        return attendanceRepository.findAllByDate(date);
    }
}

class WebUtil {
    static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return result;
        }
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
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

    static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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

    static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }
}

class HomeController {
    private final AttendanceService service;
    private final UserRepository userRepository;
    private final Long loginUserId;

    HomeController(AttendanceService service, UserRepository userRepository, Long loginUserId) {
        this.service = service;
        this.userRepository = userRepository;
        this.loginUserId = loginUserId;
    }

    void getHome(HttpExchange exchange) throws IOException {
        Map<String, String> query = WebUtil.parseQuery(exchange.getRequestURI().getRawQuery());
        String message = query.getOrDefault("message", "");
        String error = query.getOrDefault("error", "");
        Attendance attendance = service.getTodayAttendance(loginUserId);
        User user = userRepository.findById(loginUserId);

        String html = """
                <!doctype html>
                <html lang="ja">
                <head><meta charset="UTF-8"><title>Phase4 Lesson3</title></head>
                <body>
                  <h1>勤怠打刻</h1>
                  <p>ログイン中: %s</p>
                  <p><a href="/attendances">本日の勤怠一覧</a></p>
                  <p>状態: %s</p>
                  <p>出勤: %s</p>
                  <p>退勤: %s</p>
                  <p style="color:green">%s</p>
                  <p style="color:red">%s</p>
                  <form method="post" action="/clock-in"><button>出勤</button></form>
                  <form method="post" action="/clock-out"><button>退勤</button></form>
                </body>
                </html>
                """.formatted(
                WebUtil.escapeHtml(user.getName()),
                WebUtil.escapeHtml(attendance.getStatus().getLabel()),
                String.valueOf(attendance.getClockInAt()),
                String.valueOf(attendance.getClockOutAt()),
                WebUtil.escapeHtml(message),
                WebUtil.escapeHtml(error));
        WebUtil.sendHtml(exchange, 200, html);
    }

    void postClockIn(HttpExchange exchange) throws IOException {
        try {
            service.clockIn(loginUserId);
            WebUtil.redirect(exchange, "/?message=" + WebUtil.urlEncode("出勤を記録しました。"));
        } catch (BusinessException e) {
            WebUtil.redirect(exchange, "/?error=" + WebUtil.urlEncode(e.getMessage()));
        }
    }

    void postClockOut(HttpExchange exchange) throws IOException {
        try {
            service.clockOut(loginUserId);
            WebUtil.redirect(exchange, "/?message=" + WebUtil.urlEncode("退勤を記録しました。"));
        } catch (BusinessException e) {
            WebUtil.redirect(exchange, "/?error=" + WebUtil.urlEncode(e.getMessage()));
        }
    }
}

class AttendanceController {
    private final AttendanceService service;
    private final UserRepository userRepository;

    AttendanceController(AttendanceService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    void getAttendances(HttpExchange exchange) throws IOException {
        List<Attendance> rows = service.getDailyAttendances(LocalDate.now());
        StringBuilder tbody = new StringBuilder();
        for (Attendance row : rows) {
            User user = userRepository.findById(row.getUserId());
            String name = (user == null) ? "(不明)" : user.getName();
            tbody.append("<tr>")
                    .append("<td>").append(WebUtil.escapeHtml(name)).append("</td>")
                    .append("<td>").append(WebUtil.escapeHtml(row.getStatus().getLabel())).append("</td>")
                    .append("<td>").append(WebUtil.escapeHtml(String.valueOf(row.getClockInAt()))).append("</td>")
                    .append("<td>").append(WebUtil.escapeHtml(String.valueOf(row.getClockOutAt()))).append("</td>")
                    .append("</tr>");
        }

        String html = """
                <!doctype html>
                <html lang="ja">
                <head><meta charset="UTF-8"><title>Attendances</title></head>
                <body>
                  <h1>本日の勤怠一覧</h1>
                  <p><a href="/">トップへ戻る</a></p>
                  <table border="1">
                    <tr><th>ユーザー</th><th>状態</th><th>出勤</th><th>退勤</th></tr>
                    %s
                  </table>
                </body>
                </html>
                """.formatted(tbody.toString());
        WebUtil.sendHtml(exchange, 200, html);
    }
}

class AppConfig {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;
    private final HomeController homeController;
    private final AttendanceController attendanceController;

    AppConfig() {
        this.userRepository = new InMemoryUserRepository();
        this.attendanceRepository = new InMemoryAttendanceRepository();
        this.attendanceService = new AttendanceService(userRepository, attendanceRepository);
        this.homeController = new HomeController(attendanceService, userRepository, 1L);
        this.attendanceController = new AttendanceController(attendanceService, userRepository);

        userRepository.save(new User(1L, "Yamada"));
        userRepository.save(new User(2L, "Suzuki"));
        attendanceService.clockIn(2L);
    }

    HomeController homeController() {
        return homeController;
    }

    AttendanceController attendanceController() {
        return attendanceController;
    }
}

public class Lesson3Main {
    public static void main(String[] args) throws IOException {
        AppConfig config = new AppConfig();
        HomeController homeController = config.homeController();
        AttendanceController attendanceController = config.attendanceController();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/".equals(path)) {
                homeController.getHome(exchange);
            } else if ("POST".equals(method) && "/clock-in".equals(path)) {
                homeController.postClockIn(exchange);
            } else if ("POST".equals(method) && "/clock-out".equals(path)) {
                homeController.postClockOut(exchange);
            } else if ("GET".equals(method) && "/attendances".equals(path)) {
                attendanceController.getAttendances(exchange);
            } else if ("GET".equals(method) && "/health".equals(path)) {
                WebUtil.sendText(exchange, 200, "OK");
            } else if ("/clock-in".equals(path) || "/clock-out".equals(path)) {
                WebUtil.sendText(exchange, 405, "Method Not Allowed");
            } else {
                WebUtil.sendText(exchange, 404, "Not Found");
            }
        });
        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
        System.out.println("Stop: Ctrl + C");
    }
}
