# Java-20B ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-20b-web-api-prep.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答

Step 5のサーバーコードは変更せず、起動した状態で次を実行する:

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

## レベル2（拡張）解答

レベル1と同じStep 5のサーバーを使う。これは404を確認するための一時的なファイル操作であり、最後のコマンドで必ず元の名前へ戻す:

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

## レベル3（実務）解答

Step 5のサーバーコードへ、次の3か所を追記する。レベル2で変更した`index.html`のファイル名は元に戻しておく。

`main`にある`/api/messages`のコンテキスト登録より後へ、コンテキストを追加:

```java
// ===== レベル3で追加 =====
// /api/countへの通信をhandleCount(...)で処理するよう登録する
server.createContext("/api/count", WebApiPrepDemo::handleCount);
```

`WebApiPrepDemo`クラス内へハンドラを追加:

```java
// ===== レベル3で追加 =====
private static void handleCount(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod(); // GETやPOSTなど、今回送られたHTTPメソッドを取得

    if (!"GET".equalsIgnoreCase(method)) { // GETでなければ
        sendMethodNotAllowed(exchange); // 405 Method Not Allowedを返す
        return; // 405を返した後は、件数を返す処理へ進まない
    }

    // STORE.size()で現在の保存件数を取得し、{"count":件数}というJSONで返す
    sendJson(exchange, 200, "{\"count\":" + STORE.size() + "}");
}
```

`MessageStore`の`list()`より後へ`size()`を追加:

```java
// ===== レベル3で追加 =====
synchronized int size() {
    return messages.size(); // 保存用リストに入っている要素数を返す
}
```

確認:

POSTリクエストで送る項目は`name`だけです。`message`や`text`は、Step 5のサーバーが`name`から作成します。

```bash
curl -i -X POST -H "Content-Type: application/json" -d '{"name":"Taro"}' http://localhost:8091/api/messages
curl -i -X POST -H "Content-Type: application/json" -d '{"name":"Jiro"}' http://localhost:8091/api/messages
curl -i http://localhost:8091/api/count
```

期待レスポンス例:
```text
HTTP/1.1 200 OK

{"count":2}
```
