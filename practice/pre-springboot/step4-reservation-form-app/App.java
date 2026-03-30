import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final int DEFAULT_PORT = 8093;
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern DATE_PATTERN = Pattern.compile("\"date\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern START_PATTERN = Pattern.compile("\"startTime\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern END_PATTERN = Pattern.compile("\"endTime\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern NOTE_PATTERN = Pattern.compile("\"note\"\\s*:\\s*\"(.*?)\"");
    private static final ReservationStore STORE = new ReservationStore();

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        server.createContext("/api/reservations", App::handleReservationsApi);
        server.createContext("/api/reservations/", App::handleReservationByIdApi);
        server.setExecutor(null);
        server.start();

        System.out.println("Reservation Form App started: http://localhost:" + port);
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

    private static void handleReservationsApi(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);

        if ("GET".equals(method)) {
            sendJson(exchange, 200, toListJson(STORE.list()));
            return;
        }

        if ("POST".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            String name = extractString(body, NAME_PATTERN).trim();
            String dateText = extractString(body, DATE_PATTERN).trim();
            String startText = extractString(body, START_PATTERN).trim();
            String endText = extractString(body, END_PATTERN).trim();
            String note = extractString(body, NOTE_PATTERN).trim();

            String validationError = validateBasic(name, dateText, startText, endText, note);
            if (validationError != null) {
                sendJson(exchange, 400, "{\"error\":\"" + escapeJson(validationError) + "\"}");
                return;
            }

            LocalDate date;
            LocalTime startTime;
            LocalTime endTime;
            try {
                date = LocalDate.parse(dateText);
                startTime = LocalTime.parse(startText);
                endTime = LocalTime.parse(endText);
            } catch (DateTimeParseException ex) {
                sendJson(exchange, 400, "{\"error\":\"日付または時刻の形式が不正です\"}");
                return;
            }

            if (!endTime.isAfter(startTime)) {
                sendJson(exchange, 400, "{\"error\":\"終了時刻は開始時刻より後にしてください\"}");
                return;
            }

            if (STORE.hasOverlap(date, startTime, endTime)) {
                sendJson(exchange, 409, "{\"error\":\"同時間帯の予約がすでに存在します\"}");
                return;
            }

            Reservation reservation = STORE.create(name, date, startTime, endTime, note);
            sendJson(exchange, 201, toJson(reservation));
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void handleReservationByIdApi(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
        String path = exchange.getRequestURI().getPath();

        if (!path.startsWith("/api/reservations/")) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }

        String idPart = path.substring("/api/reservations/".length()).trim();
        if (idPart.isBlank()) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idPart);
        } catch (NumberFormatException ex) {
            sendJson(exchange, 400, "{\"error\":\"invalid id\"}");
            return;
        }

        if ("DELETE".equals(method)) {
            boolean deleted = STORE.delete(id);
            if (!deleted) {
                sendJson(exchange, 404, "{\"error\":\"reservation not found\"}");
                return;
            }
            sendJson(exchange, 200, "{\"message\":\"cancelled\"}");
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static String validateBasic(String name, String dateText, String startText, String endText, String note) {
        if (name.isBlank()) {
            return "名前を入力してください";
        }
        if (name.length() > 40) {
            return "名前は40文字以内で入力してください";
        }
        if (dateText.isBlank()) {
            return "日付を入力してください";
        }
        if (startText.isBlank() || endText.isBlank()) {
            return "開始時刻と終了時刻を入力してください";
        }
        if (note.length() > 100) {
            return "メモは100文字以内で入力してください";
        }
        return null;
    }

    private static String extractString(String body, Pattern pattern) {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1)
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t");
    }

    private static String toListJson(List<Reservation> reservations) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < reservations.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(toJson(reservations.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toJson(Reservation reservation) {
        return "{"
            + "\"id\":" + reservation.id + ","
            + "\"name\":\"" + escapeJson(reservation.name) + "\","
            + "\"date\":\"" + reservation.date + "\","
            + "\"startTime\":\"" + reservation.startTime + "\","
            + "\"endTime\":\"" + reservation.endTime + "\","
            + "\"note\":\"" + escapeJson(reservation.note) + "\","
            + "\"createdAt\":\"" + reservation.createdAt + "\""
            + "}";
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

    private record Reservation(
        long id,
        String name,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String note,
        LocalDateTime createdAt
    ) {
    }

    private static final class ReservationStore {
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<Reservation> reservations = new ArrayList<>();

        public synchronized List<Reservation> list() {
            List<Reservation> copy = new ArrayList<>(reservations);
            copy.sort(Comparator
                .comparing(Reservation::date)
                .thenComparing(Reservation::startTime)
                .thenComparing(Reservation::id));
            return copy;
        }

        public synchronized Reservation create(String name, LocalDate date, LocalTime startTime, LocalTime endTime, String note) {
            Reservation reservation = new Reservation(
                sequence.incrementAndGet(),
                name,
                date,
                startTime,
                endTime,
                note,
                LocalDateTime.now()
            );
            reservations.add(reservation);
            return reservation;
        }

        public synchronized boolean hasOverlap(LocalDate date, LocalTime startTime, LocalTime endTime) {
            for (Reservation reservation : reservations) {
                if (!reservation.date.equals(date)) {
                    continue;
                }
                boolean overlap = startTime.isBefore(reservation.endTime) && endTime.isAfter(reservation.startTime);
                if (overlap) {
                    return true;
                }
            }
            return false;
        }

        public synchronized boolean delete(long id) {
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).id == id) {
                    reservations.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}
