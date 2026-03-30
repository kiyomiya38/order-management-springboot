import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final int DEFAULT_PORT = 8091;
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern TITLE_PATTERN = Pattern.compile("\"title\"\\s*:\\s*\"(.*?)\"");
    private static final TodoStore STORE = new TodoStore();

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        server.createContext("/api/todos", App::handleTodos);
        server.createContext("/api/todos/", App::handleTodoById);
        server.setExecutor(null);
        server.start();

        System.out.println("ToDo Lite started: http://localhost:" + port);
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

    private static void handleTodos(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        if ("GET".equals(method)) {
            List<Todo> todos = STORE.list();
            sendJson(exchange, 200, toJsonList(todos));
            return;
        }

        if ("POST".equals(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String title = extractTitle(body).trim();
            if (title.isEmpty()) {
                sendJson(exchange, 400, "{\"error\":\"title is required\"}");
                return;
            }
            Todo created = STORE.create(title);
            sendJson(exchange, 201, toJson(created));
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void handleTodoById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod().toUpperCase();
        String suffix = path.substring("/api/todos/".length());

        if (suffix.isBlank()) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }

        if (suffix.endsWith("/toggle")) {
            String idPart = suffix.substring(0, suffix.length() - "/toggle".length());
            long id = parseId(idPart);
            if (id < 0) {
                sendJson(exchange, 400, "{\"error\":\"invalid id\"}");
                return;
            }
            if (!"PATCH".equals(method)) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Todo updated = STORE.toggle(id);
            if (updated == null) {
                sendJson(exchange, 404, "{\"error\":\"todo not found\"}");
                return;
            }
            sendJson(exchange, 200, toJson(updated));
            return;
        }

        long id = parseId(suffix);
        if (id < 0) {
            sendJson(exchange, 400, "{\"error\":\"invalid id\"}");
            return;
        }

        if ("DELETE".equals(method)) {
            boolean deleted = STORE.delete(id);
            if (!deleted) {
                sendJson(exchange, 404, "{\"error\":\"todo not found\"}");
                return;
            }
            sendJson(exchange, 200, "{\"message\":\"deleted\"}");
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static long parseId(String idPart) {
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String extractTitle(String body) {
        Matcher matcher = TITLE_PATTERN.matcher(body);
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

    private static String toJsonList(List<Todo> todos) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < todos.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(toJson(todos.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toJson(Todo todo) {
        return "{"
            + "\"id\":" + todo.id + ","
            + "\"title\":\"" + escapeJson(todo.title) + "\","
            + "\"completed\":" + todo.completed
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

    private record Todo(long id, String title, boolean completed) {
    }

    private static final class TodoStore {
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<Todo> todos = new ArrayList<>();

        public synchronized List<Todo> list() {
            return new ArrayList<>(todos);
        }

        public synchronized Todo create(String title) {
            Todo todo = new Todo(sequence.incrementAndGet(), title, false);
            todos.add(todo);
            return todo;
        }

        public synchronized Todo toggle(long id) {
            for (int i = 0; i < todos.size(); i++) {
                Todo current = todos.get(i);
                if (current.id == id) {
                    Todo updated = new Todo(current.id, current.title, !current.completed);
                    todos.set(i, updated);
                    return updated;
                }
            }
            return null;
        }

        public synchronized boolean delete(long id) {
            for (int i = 0; i < todos.size(); i++) {
                if (todos.get(i).id == id) {
                    todos.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}
