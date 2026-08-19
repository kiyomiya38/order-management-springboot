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

### サーバーコードを役割ごとに読む

Step 2のコードは長いため、上から1行ずつ暗記するのではなく、次の5つの役割に分けて読みます。

| 役割 | 主なコード | 確認すること |
| --- | --- | --- |
| 起動設定 | `main(...)`、`createContext(...)` | URLと処理を結び付ける |
| リクエスト判定 | `getRequestMethod()` | `GET`と`POST`を受け分ける |
| 入力処理 | `readAllBytes()`、`extract(...)` | POST本文から値を取り出す |
| データ保存 | `MessageStore` | 登録したメッセージを一覧として保持する |
| レスポンス | `sendJson(...)`など | ステータス、Content-Type、本文を返す |

1回のHTTP通信は次の順序で進みます。

```text
curlでリクエスト
  ↓
createContextで登録したハンドラが呼ばれる
  ↓
HTTPメソッドと入力内容を確認する
  ↓
必要ならMessageStoreを読み書きする
  ↓
ステータスとレスポンス本文を返す
```

`WebApiPrepDemo::handleHealth`はメソッド参照です。「`/api/health`へアクセスされたら`handleHealth(...)`を呼ぶ」と読めれば、この章では十分です。

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

### Step 2: `GET /api/health`だけの最小サーバーを作る
`WebApiPrepDemo.java` を次の内容で作成:

```java
import com.sun.net.httpserver.HttpExchange; // 1回分のHTTP通信情報
import com.sun.net.httpserver.HttpServer; // Java標準の簡易HTTPサーバー

import java.io.IOException; // 入出力処理で発生するchecked例外
import java.net.InetSocketAddress; // 待ち受けるポート番号を指定する型
import java.nio.charset.StandardCharsets; // UTF-8を表す定数

public class WebApiPrepDemo {
    private static final int PORT = 8091; // サーバーが待ち受けるポート番号

    public static void main(String[] args) throws IOException {
        // InetSocketAddress(PORT)でポート8091を表す接続先情報を作る
        // HttpServer.create(...)で、そのポートを使うHTTPサーバーを作る
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // createContext(URL, 担当処理)で、URLと呼び出すメソッドを結び付ける
        // 「/api/healthへアクセスされたらhandleHealth(...)を呼ぶ」と読む
        server.createContext("/api/health", WebApiPrepDemo::handleHealth);

        server.setExecutor(null); // nullを指定すると既定の実行方式を使用する
        server.start(); // HTTPリクエストの受付を開始する

        System.out.println("started: http://localhost:" + PORT);
    }

    // HttpExchangeには、1回分のリクエストとレスポンスの情報が入ってくる
    private static void handleHealth(HttpExchange exchange) throws IOException {
        // getRequestMethod()でGETやPOSTなどのHTTPメソッドを取得する
        String method = exchange.getRequestMethod();

        // equalsIgnoreCase(...)は、大文字と小文字を区別せず文字列を比較する
        if (!"GET".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return; // 405を返した後は、後続の200処理を実行しない
        }

        sendJson(exchange, 200, "{\"status\":\"OK\",\"message\":\"ready\"}");
    }

    // HTTPステータスとJSON本文を返す共通メソッド
    private static void sendJson(HttpExchange exchange, int status, String json)
            throws IOException {
        // HTTPの本文はバイト列で送るため、JSON文字列をUTF-8のbyte[]へ変換する
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        // getResponseHeaders()でレスポンスヘッダーを取得し、本文がJSONであると設定する
        exchange.getResponseHeaders()
                .set("Content-Type", "application/json; charset=UTF-8");

        // sendResponseHeaders(ステータス, 本文サイズ)でレスポンスの先頭部分を送る
        exchange.sendResponseHeaders(status, body.length);

        // getResponseBody()で本文の書き込み先を取得し、JSONのバイト列を書き込む
        exchange.getResponseBody().write(body);

        exchange.close(); // レスポンスを書き終えたため、このHTTP通信を終了する
    }
}
```

実行:

```bash
javac -encoding UTF-8 WebApiPrepDemo.java
java WebApiPrepDemo
```

別ターミナルで確認:

```bash
curl -i http://localhost:8091/api/health
```

期待レスポンス例:

```text
HTTP/1.1 200 OK

{"status":"OK","message":"ready"}
```

確認後は、サーバーを起動したターミナルで`Ctrl + C`を押して停止します。

### Step 3: `static/index.html`を返す
Step 2へ、トップページを返す処理と、ファイルが存在しない場合の404処理を追加します。`WebApiPrepDemo.java` を次の内容に更新:

```java
// ===== Step 3 で追加・変更 =====
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; // ファイルの存在確認と読み込みに使う
import java.nio.file.Path; // ファイルやディレクトリの場所を表す

public class WebApiPrepDemo {
    private static final int PORT = 8091;
    private static final Path STATIC_DIR = Path.of("static"); // staticディレクトリを表す

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 「/」へのアクセスはhandleRoot(...)が担当する
        server.createContext("/", WebApiPrepDemo::handleRoot);
        server.createContext("/api/health", WebApiPrepDemo::handleHealth);

        server.setExecutor(null);
        server.start();

        System.out.println("started: http://localhost:" + PORT);
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange);
            return;
        }

        // getRequestURI()でリクエストURLの情報を取得する
        // getPath()で、そのURLから「/」などのパス部分を取得する
        String requestPath = exchange.getRequestURI().getPath();
        if (!"/".equals(requestPath)) {
            sendNotFound(exchange);
            return;
        }

        sendStatic(exchange, "index.html", "text/html; charset=UTF-8");
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange);
            return;
        }

        sendJson(exchange, 200, "{\"status\":\"OK\",\"message\":\"ready\"}");
    }

    private static void sendStatic(
            HttpExchange exchange, String fileName, String contentType)
            throws IOException {
        // resolve(fileName)で、staticディレクトリの下にあるファイルのPathを作る
        Path file = STATIC_DIR.resolve(fileName);

        // Files.exists(file)で、指定したファイルが実際に存在するか確認する
        if (!Files.exists(file)) {
            sendNotFound(exchange);
            return;
        }

        // Files.readAllBytes(file)で、ファイル内容をすべてbyte[]として読み込む
        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
    }

    private static void sendJson(HttpExchange exchange, int status, String json)
            throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders()
                .set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}
// ===== Step 3 で追加・変更ここまで =====
```

再コンパイル・再起動:

```bash
javac -encoding UTF-8 WebApiPrepDemo.java
java WebApiPrepDemo
```

別ターミナルで確認:

```bash
curl -i http://localhost:8091/
```

期待状態:
- `HTTP/1.1 200 OK`が返る
- 本文に`<h1>Web API Prep</h1>`が含まれる

確認後は`Ctrl + C`でサーバーを停止します。

### Step 4: `GET /api/messages`で一覧を返す
このStepでは、メッセージ1件分を表す`Message`と、一覧を保存する`MessageStore`を追加します。登録処理はまだ追加しないため、GETの結果は空のJSON配列`[]`になります。`WebApiPrepDemo.java` を次の内容に更新:

```java
// ===== Step 4 で追加・変更 =====
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList; // 要素を追加できるListの実装
import java.util.List; // 複数のMessageを扱うリスト型

public class WebApiPrepDemo {
    private static final int PORT = 8091;
    private static final Path STATIC_DIR = Path.of("static");
    private static final MessageStore STORE = new MessageStore(); // メモリ上の保存先

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", WebApiPrepDemo::handleRoot);
        server.createContext("/api/health", WebApiPrepDemo::handleHealth);

        // GET /api/messagesをhandleMessages(...)へ結び付ける
        server.createContext("/api/messages", WebApiPrepDemo::handleMessages);

        server.setExecutor(null);
        server.start();
        System.out.println("started: http://localhost:" + PORT);
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

        sendStatic(exchange, "index.html", "text/html; charset=UTF-8");
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange);
            return;
        }

        sendJson(exchange, 200, "{\"status\":\"OK\",\"message\":\"ready\"}");
    }

    private static void handleMessages(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendMethodNotAllowed(exchange);
            return;
        }

        List<Message> messages = STORE.list(); // 保存済みメッセージのコピーを取得する
        String json = toListJson(messages); // List<Message>をJSON配列文字列へ変換する
        sendJson(exchange, 200, json);
    }

    private static void sendStatic(
            HttpExchange exchange, String fileName, String contentType)
            throws IOException {
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

    // Messageの一覧を[{"id":...}, ...]というJSON配列文字列へ変換する
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

    // JSON内で特別な意味を持つ「\」と「"」の前へ「\」を付ける
    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
    }

    private static void sendJson(HttpExchange exchange, int status, String json)
            throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders()
                .set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    // recordはデータをまとめて保持する型。Messageはメッセージ1件分を表す
    private record Message(long id, String name, String text) {
    }

    // 外側のWebApiPrepDemoだけで使用する、メモリ上の保存クラス
    private static final class MessageStore {
        private final List<Message> messages = new ArrayList<>();

        // synchronizedは、同時アクセスによる読み書きの衝突を防ぐために付ける
        synchronized List<Message> list() {
            // new ArrayList<>(messages)でコピーを作り、内部のListを直接渡さない
            return new ArrayList<>(messages);
        }
    }
}
// ===== Step 4 で追加・変更ここまで =====
```

再コンパイル・再起動:

```bash
javac -encoding UTF-8 WebApiPrepDemo.java
java WebApiPrepDemo
```

別ターミナルで確認:

```bash
curl -i http://localhost:8091/api/messages
```

期待レスポンス例:

```text
HTTP/1.1 200 OK

[]
```

確認後は`Ctrl + C`でサーバーを停止します。

### Step 5: `POST /api/messages`で登録できる完成コードを作る

Step 4の`WebApiPrepDemo.java`を次の内容に更新します。Step 4までの処理を残したまま、`POST /api/messages`による登録処理を追加した完成コードです。

```java
// ===== Step 5 で追加・変更する完成コード =====
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
    // {"name":"Taro"}のnameに対応する値を取り出すための検索ルール
    // (.*?)の丸括弧で囲まれた部分が、後のmatcher.group(1)で取得できる
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"");
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
            // getRequestBody()でPOSTされた本文を受け取り、readAllBytes()で全体をバイト列として読む
            byte[] requestBytes = exchange.getRequestBody().readAllBytes();
            // 受け取ったバイト列をUTF-8の文字列へ変換する
            String body = new String(requestBytes, StandardCharsets.UTF_8);
            // JSONからnameの値を取り出し、前後の空白をtrim()で除く
            String name = extractName(body).trim();

            if (name.isEmpty()) { // nameがない、または空文字・空白だけなら入力不正
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
        exchange.getResponseHeaders().set("Content-Type", contentType); // レスポンスのデータ形式を設定
        exchange.sendResponseHeaders(200, body.length); // 状態コードと本文サイズを送信
        exchange.getResponseBody().write(body); // レスポンス本文を送信
        exchange.close(); // 1回分のHTTP通信を終了
    }

    private static String extractName(String body) {
        Matcher matcher = NAME_PATTERN.matcher(body); // 本文をNAME_PATTERNで検索する準備
        if (!matcher.find()) { // nameに対応する値が見つからなかった場合
            return ""; // 呼び出し元で入力不正として扱えるよう空文字を返す
        }
        return matcher.group(1); // (.*?)に一致したnameの値だけを返す
    }

    private static String toListJson(List<Message> messages) {
        StringBuilder builder = new StringBuilder(); // JSON文字列を少しずつ組み立てる
        builder.append("["); // JSON配列の開始
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i); // i番目のメッセージを取得
            builder.append("{")
                    .append("\"id\":").append(message.id()).append(",")
                    .append("\"name\":\"").append(escapeJson(message.name())).append("\",")
                    .append("\"text\":\"").append(escapeJson(message.text())).append("\"")
                    .append("}");

            if (i < messages.size() - 1) { // 最後の要素でなければ
                builder.append(","); // 次の要素と区切るカンマを追加
            }
        }
        builder.append("]"); // JSON配列の終了
        return builder.toString(); // StringBuilderを通常のStringへ変換
    }

    private static String toCreatedJson(Message message) {
        // 登録直後は、登録結果として画面に表示しやすいよう本文の項目名をmessageにする
        return "{"
                + "\"id\":" + message.id() + ","
                + "\"name\":\"" + escapeJson(message.name()) + "\","
                + "\"message\":\"" + escapeJson(message.text()) + "\""
                + "}";
    }

    private static String escapeJson(String value) {
        // JSON文字列を壊さないよう、\と"の前へ\を付ける
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8); // JSON文字列を送信用のバイト列へ変換
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8"); // JSON形式を通知
        exchange.sendResponseHeaders(status, body.length); // 状態コードと本文サイズを送信
        exchange.getResponseBody().write(body); // JSONの本文を送信
        exchange.close(); // 1回分のHTTP通信を終了
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
            String text = "Hello, " + name; // 受け取ったnameから保存する本文を作る
            long id = sequence.incrementAndGet(); // 現在の番号を1増やし、その値を新しいIDにする
            Message message = new Message(id, name, text); // 保存する1件分のデータを作る
            messages.add(message); // メモリ上の一覧へ追加
            return message; // 登録結果のレスポンス作成に使うため、追加したデータを返す
        }
    }
}
// ===== Step 5 で追加・変更する完成コード ここまで =====
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

#### リクエストとレスポンスで使うJSON項目

このハンズオンでは、登録時と一覧取得時でJSONの目的が異なるため、項目名も一部異なります。

| 場面 | 項目 | 意味 |
|---|---|---|
| POSTリクエスト | `name` | 利用者が入力してサーバーへ送る名前 |
| POSTレスポンス | `message` | 「登録できた内容」として画面に表示するメッセージ |
| GETレスポンスの一覧 | `text` | サーバー内に保存されているメッセージ本文 |

POSTでは`{"name":"Taro"}`だけを送ります。`message`や`text`は、受け取った`name`を使ってサーバー側が作る値です。この項目構成は、後続の`web-app（簡易版）`で使用するAPIとのつながりを確認しやすくするために分けています。

### Step 6: `curl`で成功レスポンスを確認する

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

### Step 7: `400`・`404`・`405`のエラーレスポンスを確認する

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

Step 5で完成したサーバーとStep 6・7の確認手順を基準に、レベル1からレベル3まで順番に進めてください。レベル1は確認のみ、レベル2のファイル名変更は一時的な確認です。`index.html`を元の名前へ戻した後、サーバーコードはそのままレベル3で拡張します。

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
レベル2で変更した`index.html`のファイル名を元へ戻してから、Step 5のサーバーコードへ次の処理を追加します。

1. `main(...)`にある`/api/messages`のコンテキスト登録より後へ、次の行を追加する。

```java
server.createContext("/api/count", WebApiPrepDemo::handleCount);
```

2. `WebApiPrepDemo`クラス内へ、GET以外を拒否して保存件数を返す次のハンドラを追加する。

```java
private static void handleCount(HttpExchange exchange) throws IOException {
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendMethodNotAllowed(exchange);
        return;
    }

    sendJson(exchange, 200, "{\"count\":" + STORE.size() + "}");
}
```

3. `MessageStore`クラスの`list()`より後へ、次の`size()`を追加する。

```java
synchronized int size() {
    return messages.size();
}
```

4. サーバーを停止して再コンパイル・再起動する。
5. 別ターミナルで、名前`Taro`と`Jiro`のデータを`POST /api/messages`へ1回ずつ送信する。
6. `GET /api/count`を実行し、保存件数を確認する。

確認コマンド:

```bash
curl -i -X POST -H "Content-Type: application/json" -d '{"name":"Taro"}' http://localhost:8091/api/messages
curl -i -X POST -H "Content-Type: application/json" -d '{"name":"Jiro"}' http://localhost:8091/api/messages
curl -i http://localhost:8091/api/count
```

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
