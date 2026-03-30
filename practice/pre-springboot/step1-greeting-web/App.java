import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final int DEFAULT_PORT = 8090;
    private static final Path STATIC_DIR = Path.of("static");
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"");

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", App::handleRoot);
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        server.createContext("/api/greeting", App::handleGreetingApi);
        server.setExecutor(null);
        server.start();

        System.out.println("Greeting Web started: http://localhost:" + port);
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
            sendMethodNotAllowed(exchange);
            return;
        }
        if (!"/".equals(exchange.getRequestURI().getPath())) {
            sendNotFound(exchange);
            return;
        }
        handleStatic(exchange, "index.html", "text/html; charset=UTF-8");
    }

    private static void handleStatic(HttpExchange exchange, String fileName, String contentType) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange);
            return;
        }
        Path file = STATIC_DIR.resolve(fileName);
        if (!Files.exists(file)) {
            sendNotFound(exchange);
            return;
        }

        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static void handleGreetingApi(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Only POST is allowed\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String name = extractName(body).trim();
        if (name.isEmpty()) {
            sendJson(exchange, 400, "{\"error\":\"name is required\"}");
            return;
        }

        String message = "こんにちは、" + name + "さん";
        String json = "{\"message\":\"" + escapeJson(message) + "\"}";
        sendJson(exchange, 200, json);
    }

    private static String extractName(String body) {
        Matcher matcher = NAME_PATTERN.matcher(body);
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

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
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
}
