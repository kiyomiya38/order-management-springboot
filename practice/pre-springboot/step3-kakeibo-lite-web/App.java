import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final int DEFAULT_PORT = 8092;
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern TYPE_PATTERN = Pattern.compile("\"type\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("\"category\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("\"amount\"\\s*:\\s*(-?\\d+)");
    private static final Pattern MEMO_PATTERN = Pattern.compile("\"memo\"\\s*:\\s*\"(.*?)\"");
    private static final LedgerStore STORE = new LedgerStore();

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        server.createContext("/api/entries", App::handleEntriesApi);
        server.createContext("/api/summary", App::handleSummaryApi);
        server.setExecutor(null);
        server.start();

        System.out.println("Kakeibo Lite Web started: http://localhost:" + port);
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

    private static void handleEntriesApi(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);

        if ("GET".equals(method)) {
            List<LedgerEntry> entries = STORE.list();
            sendJson(exchange, 200, toEntriesJson(entries));
            return;
        }

        if ("POST".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            String type = extractString(body, TYPE_PATTERN).trim().toUpperCase(Locale.ROOT);
            String category = extractString(body, CATEGORY_PATTERN).trim();
            int amount = extractInt(body, AMOUNT_PATTERN);
            String memo = extractString(body, MEMO_PATTERN).trim();

            String error = validate(type, category, amount, memo);
            if (error != null) {
                sendJson(exchange, 400, "{\"error\":\"" + escapeJson(error) + "\"}");
                return;
            }

            LedgerEntry created = STORE.create(type, category, amount, memo);
            sendJson(exchange, 201, toEntryJson(created));
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void handleSummaryApi(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        Summary summary = STORE.summary();
        String json = "{"
            + "\"income\":" + summary.income + ","
            + "\"expense\":" + summary.expense + ","
            + "\"balance\":" + summary.balance
            + "}";
        sendJson(exchange, 200, json);
    }

    private static String validate(String type, String category, int amount, String memo) {
        if (!"INCOME".equals(type) && !"EXPENSE".equals(type)) {
            return "type は INCOME または EXPENSE を指定してください";
        }
        if (category.isBlank()) {
            return "カテゴリを入力してください";
        }
        if (category.length() > 30) {
            return "カテゴリは30文字以内で入力してください";
        }
        if (amount <= 0) {
            return "金額は1以上を入力してください";
        }
        if (amount > 1_000_000_000) {
            return "金額が大きすぎます";
        }
        if (memo.length() > 100) {
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

    private static int extractInt(String body, Pattern pattern) {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            return 0;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static String toEntriesJson(List<LedgerEntry> entries) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(toEntryJson(entries.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toEntryJson(LedgerEntry entry) {
        return "{"
            + "\"id\":" + entry.id + ","
            + "\"type\":\"" + escapeJson(entry.type) + "\","
            + "\"category\":\"" + escapeJson(entry.category) + "\","
            + "\"amount\":" + entry.amount + ","
            + "\"memo\":\"" + escapeJson(entry.memo) + "\","
            + "\"createdAt\":\"" + escapeJson(entry.createdAt) + "\""
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

    private record LedgerEntry(long id, String type, String category, int amount, String memo, String createdAt) {
    }

    private static final class Summary {
        private final int income;
        private final int expense;
        private final int balance;

        private Summary(int income, int expense) {
            this.income = income;
            this.expense = expense;
            this.balance = income - expense;
        }
    }

    private static final class LedgerStore {
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<LedgerEntry> entries = new ArrayList<>();

        public synchronized LedgerEntry create(String type, String category, int amount, String memo) {
            LedgerEntry entry = new LedgerEntry(
                sequence.incrementAndGet(),
                type,
                category,
                amount,
                memo,
                LocalDateTime.now().toString()
            );
            entries.add(entry);
            return entry;
        }

        public synchronized List<LedgerEntry> list() {
            return new ArrayList<>(entries);
        }

        public synchronized Summary summary() {
            int income = 0;
            int expense = 0;
            for (LedgerEntry entry : entries) {
                if ("INCOME".equals(entry.type)) {
                    income += entry.amount;
                } else if ("EXPENSE".equals(entry.type)) {
                    expense += entry.amount;
                }
            }
            return new Summary(income, expense);
        }
    }
}
