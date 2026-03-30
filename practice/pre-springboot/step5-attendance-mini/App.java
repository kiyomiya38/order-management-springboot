import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final int DEFAULT_PORT = 8094;
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"userId\"\\s*:\\s*(\\d+)");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final UserStore USER_STORE = new UserStore();
    private static final AttendanceStore ATTENDANCE_STORE = new AttendanceStore();

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);
        seedUsers();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        server.createContext("/api/users", App::handleUsers);
        server.createContext("/api/users/", App::handleUserById);
        server.createContext("/api/attendance/today", App::handleToday);
        server.createContext("/api/attendance/history", App::handleHistory);
        server.createContext("/api/attendance/clock-in", App::handleClockIn);
        server.createContext("/api/attendance/clock-out", App::handleClockOut);
        server.setExecutor(null);
        server.start();

        System.out.println("Attendance Mini started: http://localhost:" + port);
    }

    private static int resolvePort(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return DEFAULT_PORT;
        }
    }

    private static void seedUsers() {
        if (!USER_STORE.list().isEmpty()) {
            return;
        }
        USER_STORE.create("user1", "ROLE_USER");
        USER_STORE.create("admin", "ROLE_ADMIN");
        USER_STORE.create("tanaka", "ROLE_USER");
        USER_STORE.create("sato", "ROLE_USER");
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        if (!"/".equals(exchange.getRequestURI().getPath())) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }
        handleStatic(exchange, "index.html", "text/html; charset=UTF-8");
    }

    private static void handleStatic(HttpExchange exchange, String fileName, String contentType) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        Path file = STATIC_DIR.resolve(fileName);
        if (!Files.exists(file)) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }

        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static void handleUsers(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        sendJson(exchange, 200, toUsersJson(USER_STORE.list()));
    }

    private static void handleUserById(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String idPart = path.substring("/api/users/".length()).trim();
        long id = parseLong(idPart);
        if (id < 0) {
            sendJson(exchange, 400, "{\"error\":\"invalid id\"}");
            return;
        }
        if (ATTENDANCE_STORE.hasAnyHistory(id)) {
            sendJson(exchange, 409, "{\"error\":\"勤怠履歴があるため削除できません\"}");
            return;
        }
        boolean deleted = USER_STORE.delete(id);
        if (!deleted) {
            sendJson(exchange, 404, "{\"error\":\"user not found\"}");
            return;
        }
        sendJson(exchange, 200, "{\"message\":\"deleted\"}");
    }

    private static void handleToday(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        long userId = parseUserIdFromQuery(exchange.getRequestURI().getRawQuery());
        if (userId < 0 || USER_STORE.find(userId) == null) {
            sendJson(exchange, 400, "{\"error\":\"valid userId is required\"}");
            return;
        }
        AttendanceRecord record = ATTENDANCE_STORE.findToday(userId);
        sendJson(exchange, 200, toTodayJson(record));
    }

    private static void handleHistory(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        long userId = parseUserIdFromQuery(exchange.getRequestURI().getRawQuery());
        if (userId < 0 || USER_STORE.find(userId) == null) {
            sendJson(exchange, 400, "{\"error\":\"valid userId is required\"}");
            return;
        }
        sendJson(exchange, 200, toHistoryJson(ATTENDANCE_STORE.history(userId)));
    }

    private static void handleClockIn(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        long userId = parseUserIdFromBody(body);
        if (userId < 0 || USER_STORE.find(userId) == null) {
            sendJson(exchange, 400, "{\"error\":\"valid userId is required\"}");
            return;
        }

        String error = ATTENDANCE_STORE.clockIn(userId);
        if (error != null) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(error) + "\"}");
            return;
        }
        sendJson(exchange, 200, "{\"message\":\"出勤しました\"}");
    }

    private static void handleClockOut(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        long userId = parseUserIdFromBody(body);
        if (userId < 0 || USER_STORE.find(userId) == null) {
            sendJson(exchange, 400, "{\"error\":\"valid userId is required\"}");
            return;
        }

        String error = ATTENDANCE_STORE.clockOut(userId);
        if (error != null) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(error) + "\"}");
            return;
        }
        sendJson(exchange, 200, "{\"message\":\"退勤しました\"}");
    }

    private static long parseUserIdFromBody(String body) {
        Matcher matcher = USER_ID_PATTERN.matcher(body);
        if (!matcher.find()) {
            return -1;
        }
        return parseLong(matcher.group(1));
    }

    private static long parseUserIdFromQuery(String query) {
        if (query == null || query.isBlank()) {
            return -1;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "userId".equals(kv[0])) {
                return parseLong(kv[1]);
            }
        }
        return -1;
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String toUsersJson(List<User> users) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{")
                .append("\"id\":").append(user.id)
                .append(",\"username\":\"").append(escapeJson(user.username)).append("\"")
                .append(",\"role\":\"").append(escapeJson(user.role)).append("\"")
                .append("}");
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toTodayJson(AttendanceRecord record) {
        if (record == null) {
            return "{"
                + "\"date\":\"" + LocalDate.now() + "\","
                + "\"status\":\"NOT_STARTED\","
                + "\"statusLabel\":\"未出勤\","
                + "\"startTime\":\"\","
                + "\"endTime\":\"\""
                + "}";
        }
        return "{"
            + "\"date\":\"" + record.workDate + "\","
            + "\"status\":\"" + record.status + "\","
            + "\"statusLabel\":\"" + escapeJson(statusLabel(record.status)) + "\","
            + "\"startTime\":\"" + formatTime(record.startTime) + "\","
            + "\"endTime\":\"" + formatTime(record.endTime) + "\""
            + "}";
    }

    private static String toHistoryJson(List<AttendanceRecord> history) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < history.size(); i++) {
            AttendanceRecord record = history.get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{")
                .append("\"id\":").append(record.id)
                .append(",\"date\":\"").append(record.workDate).append("\"")
                .append(",\"status\":\"").append(record.status).append("\"")
                .append(",\"statusLabel\":\"").append(escapeJson(statusLabel(record.status))).append("\"")
                .append(",\"startTime\":\"").append(formatTime(record.startTime)).append("\"")
                .append(",\"endTime\":\"").append(formatTime(record.endTime)).append("\"")
                .append("}");
        }
        builder.append("]");
        return builder.toString();
    }

    private static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMAT);
    }

    private static String statusLabel(AttendanceStatus status) {
        return switch (status) {
            case WORKING -> "出勤中";
            case FINISHED -> "退勤済み";
            default -> "未出勤";
        };
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private record User(long id, String username, String role) {
    }

    private enum AttendanceStatus {
        NOT_STARTED,
        WORKING,
        FINISHED
    }

    private static final class AttendanceRecord {
        private final long id;
        private final long userId;
        private final LocalDate workDate;
        private final LocalTime startTime;
        private LocalTime endTime;
        private AttendanceStatus status;

        private AttendanceRecord(long id, long userId, LocalDate workDate, LocalTime startTime) {
            this.id = id;
            this.userId = userId;
            this.workDate = workDate;
            this.startTime = startTime;
            this.status = AttendanceStatus.WORKING;
        }
    }

    private static final class UserStore {
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<User> users = new ArrayList<>();

        public synchronized User create(String username, String role) {
            User user = new User(sequence.incrementAndGet(), username, role);
            users.add(user);
            return user;
        }

        public synchronized List<User> list() {
            List<User> copy = new ArrayList<>(users);
            copy.sort(Comparator.comparing(User::id));
            return copy;
        }

        public synchronized User find(long id) {
            for (User user : users) {
                if (user.id == id) {
                    return user;
                }
            }
            return null;
        }

        public synchronized boolean delete(long id) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == id) {
                    users.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    private static final class AttendanceStore {
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<AttendanceRecord> records = new ArrayList<>();

        public synchronized AttendanceRecord findToday(long userId) {
            LocalDate today = LocalDate.now();
            for (AttendanceRecord record : records) {
                if (record.userId == userId && record.workDate.equals(today)) {
                    return record;
                }
            }
            return null;
        }

        public synchronized String clockIn(long userId) {
            AttendanceRecord today = findToday(userId);
            if (today != null) {
                if (today.status == AttendanceStatus.FINISHED) {
                    return "本日はすでに退勤済みです";
                }
                return "本日はすでに出勤済みです";
            }
            records.add(new AttendanceRecord(sequence.incrementAndGet(), userId, LocalDate.now(), LocalTime.now()));
            return null;
        }

        public synchronized String clockOut(long userId) {
            AttendanceRecord today = findToday(userId);
            if (today == null) {
                return "未出勤のため退勤できません";
            }
            if (today.status == AttendanceStatus.FINISHED) {
                return "すでに退勤済みです";
            }
            today.endTime = LocalTime.now();
            today.status = AttendanceStatus.FINISHED;
            return null;
        }

        public synchronized List<AttendanceRecord> history(long userId) {
            List<AttendanceRecord> result = new ArrayList<>();
            for (AttendanceRecord record : records) {
                if (record.userId == userId) {
                    result.add(record);
                }
            }
            result.sort(Comparator
                .comparing((AttendanceRecord record) -> record.workDate).reversed()
                .thenComparing(record -> record.startTime, Comparator.nullsLast(Comparator.reverseOrder())));
            return result;
        }

        public synchronized boolean hasAnyHistory(long userId) {
            for (AttendanceRecord record : records) {
                if (record.userId == userId) {
                    return true;
                }
            }
            return false;
        }
    }
}
