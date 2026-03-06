import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class MiniWebServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
        server.createContext("/", MiniWebServer::handleTop);
        server.start();
        System.out.println("Server started: http://localhost:8080/");
    }

    private static void handleTop(HttpExchange exchange) throws IOException {
        String html = "<!doctype html><html lang=\"ja\"><head><meta charset=\"utf-8\"><title>Mini</title></head>"
                + "<body><h1>Day0 Manual Web</h1><p>手動でWeb起動しています。</p></body></html>";
        byte[] body = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}