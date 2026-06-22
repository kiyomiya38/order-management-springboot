# Java-20B ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-20b-web-api-prep.md`

## ミニ演習解答
1. `/api/health` に `POST` でアクセス:

```bash
curl -i -X POST http://localhost:8091/api/health
```

期待レスポンス例:
```text
HTTP/1.1 405 Method Not Allowed

{"error":"Method Not Allowed"}
```

理由:
- `/api/health` は存在するが、`GET` だけ許可しているため

2. `static/index.html` のファイル名を一時変更:

```bash
mv static/index.html static/index-old.html
curl -i http://localhost:8091/
mv static/index-old.html static/index.html
```

期待レスポンス例:
```text
HTTP/1.1 404 Not Found

{"error":"Not Found"}
```

理由:
- `sendStatic(...)` 内の `Files.exists(file)` が `false` になるため

3. `GET /api/count` の追加例:

`main` にコンテキストを追加:

```java
server.createContext("/api/count", WebApiPrepDemo::handleCount);
```

ハンドラを追加:

```java
private static void handleCount(HttpExchange exchange) throws IOException {
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendMethodNotAllowed(exchange);
        return;
    }

    sendJson(exchange, 200, "{\"count\":" + STORE.size() + "}");
}
```

`MessageStore` に `size()` を追加:

```java
synchronized int size() {
    return messages.size();
}
```

確認:

```bash
curl -i http://localhost:8091/api/count
```

期待レスポンス例:
```text
HTTP/1.1 200 OK

{"count":登録済み件数}
```
