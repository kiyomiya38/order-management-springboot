# Java-20B 補講: Web API前準備（HttpServer + POST + MessageStore）

## 1. この資料のゴール
- `HttpServer` で `GET` / `POST` を受け分けできる
- HTTPステータス（`200` / `201` / `400` / `404` / `405`）を返す場面を説明できる
- `Files.exists` / `Files.readAllBytes` で静的ファイルを返せる
- `AtomicLong` / `synchronized` を、`web-app(簡易版)` Lesson2の `MessageStore` を読むための最小範囲で理解する

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- Java-18〜20およびJava-20Aの内容を実施済み
- `curl` をGit Bashで実行できる

---

## 3. 先に覚えるポイント
1. `HttpServer` はURLごとに処理を登録できる
2. `GET` は取得、`POST` は登録や送信に使うことが多い
3. `exchange.getRequestMethod()` でHTTPメソッドを確認する
4. `exchange.getRequestBody().readAllBytes()` でPOST本文を読む
5. `Files.exists(...)` でファイル有無を確認し、`Files.readAllBytes(...)` でファイル内容を読む
6. `AtomicLong` は連番IDを作るためのカウンタとして使う
7. `synchronized` は同時アクセス時に、リストの読み書きがぶつからないようにする

### HTTPステータスの使い分け
| ステータス | 使う場面 |
| --- | --- |
| `200 OK` | 取得成功 |
| `201 Created` | 登録成功 |
| `400 Bad Request` | 入力内容が不正 |
| `404 Not Found` | URLやファイルが存在しない |
| `405 Method Not Allowed` | URLはあるがHTTPメソッドが許可されていない |

補足:
- この補講では、JSONを正規表現で最小限だけ取り出します。
- 実務では Jackson などのJSONライブラリを使うことが多いです。
- `AtomicLong` / `synchronized` は詳しい並行処理ではなく、`web-app(簡易版)` Lesson2のコードを読むための最小説明です。

---

## 4. ハンズオン

目的:
- `web-app(簡易版)` Lesson2のJavaコードより小さい構成で、Web APIの入口処理を確認する

完了条件:
- `GET /api/health`、`GET /api/messages`、`POST /api/messages` を `curl` で確認できる
- `400` / `404` / `405` を再現できる

作成ファイル:
- `~/order-management-springboot/practice/java/handson20b/WebApiPrepDemo.java`
- `~/order-management-springboot/practice/java/handson20b/static/index.html`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson20b/static
cd ~/order-management-springboot/practice/java/handson20b
```

### Step 1: 静的HTMLを作る
`static/index.html` を次の内容で作成:

```html
<!doctype html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <title>Web API Prep</title>
</head>
<body>
  <h1>Web API Prep</h1>
  <p>Java HttpServer の事前確認ページです。</p>
</body>
</html>
```

### Step 2: `GET` / `POST` を扱うHTTPサーバーを作る
`WebApiPrepDemo.java` を次の内容で作成:

```java
import com.sun.net.httpserver.HttpExchange; // 1回分のHTTP通信情報
import com.sun.net.httpserver.HttpServer; // Java標準の簡易HTTPサーバー

import java.io.IOException; // 入出力例外
import java.net.InetSocketAddress; // ポート指定
import java.nio.charset.StandardCharsets; // UTF-8
import java.nio.file.Files; // ファイル存在確認と読み込み
import java.nio.file.Path; // ファイルパス
import java.util.ArrayList; // 可変長リスト
import java.util.List; // リスト型
import java.util.concurrent.atomic.AtomicLong; // 連番IDカウンタ
import java.util.regex.Matcher; // 正規表現の検索結果
import java.util.regex.Pattern; // 正規表現パターン

public class WebApiPrepDemo { // WebアプリLesson2前のWeb API確認クラス
    private static final int PORT = 8091; // 待受ポート
    private static final Path STATIC_DIR = Path.of("static"); // 静的ファイル置き場
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\""); // JSONのname抽出
    private static final MessageStore STORE = new MessageStore(); // メモリ上の保存先

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0); // HTTPサーバー作成
        server.createContext("/", WebApiPrepDemo::handleRoot); // トップページ
        server.createContext("/api/health", WebApiPrepDemo::handleHealth); // 起動確認API
        server.createContext("/api/messages", WebApiPrepDemo::handleMessages); // 一覧/登録API
        server.setExecutor(null); // 既定の実行方式
        server.start(); // 受付開始

        System.out.println("started: http://localhost:" + PORT); // 起動確認
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) { // HTML取得はGETのみ
            sendMethodNotAllowed(exchange);
            return;
        }

        if (!"/".equals(exchange.getRequestURI().getPath())) { // 未登録パスは404
            sendNotFound(exchange);
            return;
        }

        sendStatic(exchange, "index.html", "text/html; charset=UTF-8"); // static/index.html を返す
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) { // healthはGETのみ
            sendMethodNotAllowed(exchange);
            return;
        }

        sendJson(exchange, 200, "{\"status\":\"OK\",\"message\":\"ready\"}"); // 200 OK
    }

    private static void handleMessages(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // GET / POST など

        if ("GET".equalsIgnoreCase(method)) { // 一覧取得
            List<Message> messages = STORE.list();
            sendJson(exchange, 200, toListJson(messages)); // 200 OK
            return;
        }

        if ("POST".equalsIgnoreCase(method)) { // 新規登録
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // POST本文
            String name = extractName(body).trim(); // JSONからnameを取り出す

            if (name.isEmpty()) {
                sendJson(exchange, 400, "{\"error\":\"name is required\"}"); // 400 Bad Request
                return;
            }

            Message message = STORE.create(name); // メモリに保存
            sendJson(exchange, 201, toCreatedJson(message)); // 201 Created
            return;
        }

        sendMethodNotAllowed(exchange); // GET/POST以外は405
    }

    private static void sendStatic(HttpExchange exchange, String fileName, String contentType) throws IOException {
        Path file = STATIC_DIR.resolve(fileName); // static配下のファイル
        if (!Files.exists(file)) {
            sendNotFound(exchange); // 404 Not Found
            return;
        }

        byte[] body = Files.readAllBytes(file); // ファイル内容をバイト列で読む
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static String extractName(String body) {
        Matcher matcher = NAME_PATTERN.matcher(body);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1);
    }

    private static String toListJson(List<Message> messages) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            builder.append("{")
                    .append("\"id\":").append(message.id()).append(",")
                    .append("\"name\":\"").append(escapeJson(message.name())).append("\",")
                    .append("\"text\":\"").append(escapeJson(message.text())).append("\"")
                    .append("}");

            if (i < messages.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toCreatedJson(Message message) {
        return "{"
                + "\"id\":" + message.id() + ","
                + "\"name\":\"" + escapeJson(message.name()) + "\","
                + "\"message\":\"" + escapeJson(message.text()) + "\""
                + "}";
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
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

    private record Message(long id, String name, String text) { // メッセージ1件分
    }

    private static final class MessageStore { // メモリ上の保存先
        private final AtomicLong sequence = new AtomicLong(); // 1, 2, 3... のIDを作る
        private final List<Message> messages = new ArrayList<>(); // 保存済みメッセージ

        synchronized List<Message> list() { // 読み取り中に書き込みとぶつからないようにする
            return new ArrayList<>(messages); // 内部リストを直接渡さずコピーを返す
        }

        synchronized Message create(String name) { // 書き込み中に他の読み書きとぶつからないようにする
            String text = "Hello, " + name;
            Message message = new Message(sequence.incrementAndGet(), name, text); // IDを1つ進める
            messages.add(message);
            return message;
        }
    }
}
```

実行:
```bash
javac -encoding UTF-8 WebApiPrepDemo.java
java WebApiPrepDemo
```

期待出力例:
```text
started: http://localhost:8091
```

終了方法:
- サーバー起動中のターミナルで `Ctrl + C`

### Step 3: `curl` で確認する
別ターミナルを開き、次のコマンドを実行する。

#### `200 OK`: 起動確認
```bash
curl -i http://localhost:8091/api/health
```

期待レスポンス例:
```text
HTTP/1.1 200 OK

{"status":"OK","message":"ready"}
```

#### `201 Created`: メッセージ登録
```bash
curl -i -X POST http://localhost:8091/api/messages \
  -H "Content-Type: application/json" \
  -d '{"name":"Taro"}'
```

期待レスポンス例:
```text
HTTP/1.1 201 Created

{"id":1,"name":"Taro","message":"Hello, Taro"}
```

#### `200 OK`: 一覧取得
```bash
curl -i http://localhost:8091/api/messages
```

期待レスポンス例:
```text
HTTP/1.1 200 OK

[{"id":1,"name":"Taro","text":"Hello, Taro"}]
```

#### `400 Bad Request`: 入力不正
```bash
curl -i -X POST http://localhost:8091/api/messages \
  -H "Content-Type: application/json" \
  -d '{"name":""}'
```

期待レスポンス例:
```text
HTTP/1.1 400 Bad Request

{"error":"name is required"}
```

#### `404 Not Found`: 存在しないURL
```bash
curl -i http://localhost:8091/api/unknown
```

期待レスポンス例:
```text
HTTP/1.1 404 Not Found

{"error":"Not Found"}
```

#### `405 Method Not Allowed`: 許可されていないメソッド
```bash
curl -i -X PUT http://localhost:8091/api/messages
```

期待レスポンス例:
```text
HTTP/1.1 405 Method Not Allowed

{"error":"Method Not Allowed"}
```

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `/api/health` に `POST` でアクセスし、`405 Method Not Allowed` になることを確認する。

期待状態:
- URLは存在するが、`POST` は許可されていないと説明できる

### レベル2（拡張）
1. `static/index.html` のファイル名を一時的に変更し、トップページ `/` が `404 Not Found` になることを確認する。
2. 確認後はファイル名を元に戻す。

期待状態:
- `Files.exists(...)` が `false` になると `404` を返す、と説明できる

### レベル3（実務）
1. `GET /api/count` を追加し、保存済みメッセージ件数をJSONで返す。

期待出力例:
```json
{"count":2}
```

---

## 6. つまずきポイント
- `POST` しているのに登録されない
  -> `exchange.getRequestMethod()` の分岐が `POST` に入っているか確認する
- `curl` のJSONが壊れる
  -> Git Bashでは `-d '{"name":"Taro"}'` のようにシングルクォートで囲む
- サーバーを起動したまま再コンパイルして反映されない
  -> `Ctrl + C` で停止し、`javac` 後に `java` で再起動する
- 一覧が再起動後に消える
  -> 今回はメモリ保存だけなので正常
- `synchronized` の意味が広すぎて分からない
  -> この補講では「同時に読み書きされても `messages` が壊れにくくする印」と読む
