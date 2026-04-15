# Lesson5 状態遷移と管理画面（勤怠ミニ）

## 目的（Lesson5でできるようになること）
- 出勤/退勤の状態遷移をサーバー側で制御できる
- 当日状態表示と履歴表示を連動させた画面を作れる
- ユーザー一覧の検索・絞り込みと削除確認ダイアログを実装できる

## 前提
- Lesson1～Lesson4 を完了している
- Git Bash を使える
- JDK 17 がインストール済み

## Lesson5で作るもの
- 画面:
  - 対象ユーザー選択
  - 本日の勤怠表示（状態/出勤時刻/退勤時刻）
  - 勤怠履歴一覧
  - ユーザー管理（検索・絞り込み・削除）
- API:
  - `GET /api/users`
  - `DELETE /api/users/{id}`
  - `GET /api/attendance/today?userId={id}`
  - `GET /api/attendance/history?userId={id}`
  - `POST /api/attendance/clock-in`
  - `POST /api/attendance/clock-out`

---

## 0. 事前確認（Git Bashで実行）

```bash
java -version
javac -version
```

---

## 1. 作業フォルダ
作業場所（絶対パス）:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini`

Git Bash:
```bash
cd ~/order-management-springboot
mkdir -p practice/pre-springboot/step5-attendance-mini/static
cd ~/order-management-springboot/practice/pre-springboot/step5-attendance-mini
```

---

## 2. ファイル構成を作成
以下の 4 ファイルを作成:

- `App.java`
- `static/index.html`
- `static/styles.css`
- `static/app.js`

---

## 3. `App.java` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/App.java`

```java
// 1回分のHTTP通信（リクエスト情報 + レスポンス出力先）を扱う型
import com.sun.net.httpserver.HttpExchange;
// Java標準の簡易HTTPサーバー
import com.sun.net.httpserver.HttpServer;

// 入出力エラー例外
import java.io.IOException;
// 待受IP/ポートの指定に使う
import java.net.InetSocketAddress;
// UTF-8などの文字コード定数
import java.nio.charset.StandardCharsets;
// ファイル存在確認/読み込み
import java.nio.file.Files;
// OS差異を吸収してパスを扱う
import java.nio.file.Path;
// 日付のみ
import java.time.LocalDate;
// 時刻のみ
import java.time.LocalTime;
// 時刻表示フォーマット
import java.time.format.DateTimeFormatter;
// 可変長リスト実装
import java.util.ArrayList;
// 並び替え条件を定義
import java.util.Comparator;
// リスト型のインターフェース
import java.util.List;
// 大文字/小文字変換のロケール指定
import java.util.Locale;
// スレッド安全に連番を採番
import java.util.concurrent.atomic.AtomicLong;
// 正規表現の検索結果
import java.util.regex.Matcher;
// 正規表現パターン
import java.util.regex.Pattern;

// アプリ全体のエントリポイントクラス
public class App {
    // 引数でポート指定が無いときに使う既定ポート
    private static final int DEFAULT_PORT = 8094;
    // HTML/CSS/JS の静的ファイルを置くディレクトリ
    private static final Path STATIC_DIR = Path.of("static");
    // JSON本文から必要項目を取り出すための正規表現
    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"userId\"\\s*:\\s*(\\d+)");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final UserStore USER_STORE = new UserStore();
    private static final AttendanceStore ATTENDANCE_STORE = new AttendanceStore();

    // アプリ起動の入口。ルーティングを登録してサーバーを開始する
    public static void main(String[] args) throws IOException {
        int port = resolvePort(args);
        seedUsers();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // ルーティング登録: "/" にアクセスされたときの処理を関連付ける
        server.createContext("/", App::handleRoot);
        // ルーティング登録: "/styles.css" にアクセスされたときの処理を関連付ける
        // exchange は今回1回分のHTTP通信情報。handleStatic に委譲して静的ファイルを返す
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8"));
        // ルーティング登録: "/app.js" にアクセスされたときの処理を関連付ける
        // exchange は今回1回分のHTTP通信情報。handleStatic に委譲して静的ファイルを返す
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8"));
        // ルーティング登録: "/api/users" にアクセスされたときの処理を関連付ける
        server.createContext("/api/users", App::handleUsers);
        // ルーティング登録: "/api/users/" にアクセスされたときの処理を関連付ける
        server.createContext("/api/users/", App::handleUserById);
        // ルーティング登録: "/api/attendance/today" にアクセスされたときの処理を関連付ける
        server.createContext("/api/attendance/today", App::handleToday);
        // ルーティング登録: "/api/attendance/history" にアクセスされたときの処理を関連付ける
        server.createContext("/api/attendance/history", App::handleHistory);
        // ルーティング登録: "/api/attendance/clock-in" にアクセスされたときの処理を関連付ける
        server.createContext("/api/attendance/clock-in", App::handleClockIn);
        // ルーティング登録: "/api/attendance/clock-out" にアクセスされたときの処理を関連付ける
        server.createContext("/api/attendance/clock-out", App::handleClockOut);
        // スレッド実行方式はデフォルト設定を使う
        server.setExecutor(null);
        // HTTPサーバーを起動して待受開始
        server.start();

        System.out.println("Attendance Mini started: http://localhost:" + port);
    }

    // 起動引数から使用ポートを決める（不正値なら既定ポート）
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

    // 初期ユーザーをメモリへ投入する
    private static void seedUsers() {
        // すでにユーザーが登録済みなら、初期データ投入はスキップする
        if (!USER_STORE.list().isEmpty()) {
            return;
        }
        USER_STORE.create("user1", "ROLE_USER");
        USER_STORE.create("admin", "ROLE_ADMIN");
        USER_STORE.create("tanaka", "ROLE_USER");
        USER_STORE.create("sato", "ROLE_USER");
    }

    // "/" へのアクセスを処理し、index.html を返す
    private static void handleRoot(HttpExchange exchange) throws IOException {
        // GET以外のHTTPメソッドは受け付けない
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

    // static配下のファイルを読み込み、指定Content-Typeで返す共通処理
    private static void handleStatic(HttpExchange exchange, String fileName, String contentType) throws IOException {
        // GET以外のHTTPメソッドは受け付けない
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        Path file = STATIC_DIR.resolve(fileName);
        // 対象ファイルが存在しない場合は404を返す
        if (!Files.exists(file)) {
            sendJson(exchange, 404, "{\"error\":\"Not Found\"}");
            return;
        }

        byte[] body = Files.readAllBytes(file);
        // ブラウザが中身を正しく解釈できるようContent-Typeを設定
        exchange.getResponseHeaders().set("Content-Type", contentType);
        // ステータスコードとボディ長を先に送信する
        exchange.sendResponseHeaders(200, body.length);
        // レスポンス本文を書き込む
        exchange.getResponseBody().write(body);
        // 通信を終了してリソースを解放する
        exchange.close();
    }

    // ユーザー一覧APIを返す
    private static void handleUsers(HttpExchange exchange) throws IOException {
        // GET以外のHTTPメソッドは受け付けない
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        sendJson(exchange, 200, toUsersJson(USER_STORE.list()));
    }

    // ユーザー削除APIを処理する
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

    // 指定ユーザーの当日勤怠を返す
    private static void handleToday(HttpExchange exchange) throws IOException {
        // GET以外のHTTPメソッドは受け付けない
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

    // 指定ユーザーの勤怠履歴を返す
    private static void handleHistory(HttpExchange exchange) throws IOException {
        // GET以外のHTTPメソッドは受け付けない
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

    // 出勤打刻を受け付ける
    private static void handleClockIn(HttpExchange exchange) throws IOException {
        // POST以外のHTTPメソッドは受け付けない
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        // リクエスト本文(JSON文字列)をUTF-8で読み取る
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

    // 退勤打刻を受け付ける
    private static void handleClockOut(HttpExchange exchange) throws IOException {
        // POST以外のHTTPメソッドは受け付けない
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        // リクエスト本文(JSON文字列)をUTF-8で読み取る
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

    // JSON本文からuserIdを抽出する
    private static long parseUserIdFromBody(String body) {
        Matcher matcher = USER_ID_PATTERN.matcher(body);
        if (!matcher.find()) {
            return -1;
        }
        return parseLong(matcher.group(1));
    }

    // クエリ文字列からuserIdを抽出する
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

    // 数値変換の共通処理（失敗時は-1）
    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    // ユーザー一覧をJSON配列へ変換する
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

    // 当日勤怠をJSONへ変換する（未打刻時も含む）
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

    // 勤怠履歴をJSON配列へ変換する
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

    // LocalTimeを "HH:mm:ss" 文字列へ整形する
    private static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMAT);
    }

    // 内部状態（enum）を画面表示用文言へ変換する
    private static String statusLabel(AttendanceStatus status) {
        return switch (status) {
            case WORKING -> "出勤中";
            case FINISHED -> "退勤済み";
            default -> "未出勤";
        };
    }

    // JSONレスポンスの共通送信処理
    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        // ブラウザが中身を正しく解釈できるようContent-Typeを設定
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        // ステータスコードとボディ長を先に送信する
        exchange.sendResponseHeaders(status, body.length);
        // レスポンス本文を書き込む
        exchange.getResponseBody().write(body);
        // 通信を終了してリソースを解放する
        exchange.close();
    }

    // JSON文字列として安全になるようにエスケープする
    private static String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    // record: ユーザー情報を不変データとして簡潔に表現する
    private record User(long id, String username, String role) {
    }

    // enum: 状態値を固定し、文字列の表記ゆれを防ぐ
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
        // AtomicLong: 同時アクセス時も安全に連番を採番する
        private final AtomicLong sequence = new AtomicLong(0);
        private final List<User> users = new ArrayList<>();

        // synchronized: 共有リスト更新の競合を防ぐ
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
```

---

## 4. `index.html` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/index.html`

```html
<!-- HTML5文書であることを宣言 -->
<!doctype html>
<!-- このページの言語設定（日本語） -->
<html lang="ja">
<!-- 画面に表示しないメタ情報をまとめる領域 -->
<head>
  <!-- 文字コードをUTF-8にして文字化けを防ぐ -->
  <meta charset="utf-8">
  <!-- スマホ表示で幅を適切に合わせる -->
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- ブラウザタブに表示されるタイトル -->
  <title>勤怠ミニ</title>
  <!-- CSSファイルを読み込む -->
  <link rel="stylesheet" href="/styles.css">
</head>
<!-- ユーザーに見える本体コンテンツ -->
<body>
  <!-- 画面の主コンテンツ領域 -->
  <main class="container">
    <!-- 画面の見出し領域 -->
    <header>
      <h1>勤怠ミニ</h1>
      <p class="muted">状態遷移 + 履歴 + ユーザー一覧UI改善</p>
    </header>

    <section class="panel">
      <div class="row">
        <label>対象ユーザー
          <select id="active-user-select"></select>
        </label>
        <span id="active-user-role" class="muted"></span>
      </div>
      <p id="global-message" class="muted"></p>
    </section>

    <section class="panel">
      <h2>本日の勤怠</h2>
      <div class="status-grid">
        <div>
          <p class="label">日付</p>
          <p id="today-date" class="value">-</p>
        </div>
        <div>
          <p class="label">状態</p>
          <p id="today-status" class="value">未出勤</p>
        </div>
        <div>
          <p class="label">出勤時刻</p>
          <p id="today-start" class="value">-</p>
        </div>
        <div>
          <p class="label">退勤時刻</p>
          <p id="today-end" class="value">-</p>
        </div>
      </div>
      <div class="row">
        <!-- 押下操作を行うボタン -->
        <button id="clock-in-btn" type="button">出勤</button>
        <!-- 押下操作を行うボタン -->
        <button id="clock-out-btn" type="button">退勤</button>
      </div>
    </section>

    <section class="panel">
      <div class="row">
        <h2>勤怠履歴</h2>
        <span id="history-count" class="muted"></span>
      </div>
      <!-- 一覧表示用テーブル -->
      <table>
        <thead>
          <tr>
            <th>日付</th>
            <th>出勤時刻</th>
            <th>退勤時刻</th>
            <th>状態</th>
          </tr>
        </thead>
        <!-- JSで行を動的に追加する領域 -->
        <tbody id="history-body"></tbody>
      </table>
    </section>

    <section class="panel">
      <div class="row">
        <h2>ユーザー管理（UI改善）</h2>
        <span id="user-count" class="muted"></span>
      </div>
      <div class="row filter-row">
        <label>ユーザー名検索
          <!-- ユーザーが値を入力する要素 -->
          <input id="user-search-input" type="search" placeholder="例: user">
        </label>
        <label>ロール絞り込み
          <select id="role-filter-select">
            <option value="">すべて</option>
            <option value="ROLE_USER">ROLE_USER</option>
            <option value="ROLE_ADMIN">ROLE_ADMIN</option>
          </select>
        </label>
      </div>
      <!-- 一覧表示用テーブル -->
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ユーザー名</th>
            <th>ロール</th>
            <th>操作</th>
          </tr>
        </thead>
        <!-- JSで行を動的に追加する領域 -->
        <tbody id="user-body"></tbody>
      </table>
    </section>
  </main>
  <!-- JavaScriptを読み込む。deferでHTML解析後に実行 -->
  <script src="/app.js" defer></script>
</body>
</html>
```

---

## 5. `styles.css` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/styles.css`

```css
/* 画面全体で再利用するデザイン変数を定義 */
:root {
  /* ページ背景色 */
  --bg: #f5f7fb;
  /* カード/パネル背景色 */
  --panel: #ffffff;
  /* 基本文字色 */
  --text: #1f2937;
  /* 補助文字色 */
  --muted: #6b7280;
  /* 枠線色 */
  --border: #d1d5db;
  /* 強調色（主ボタン等） */
  --accent: #0284c7;
  /* 危険操作色（削除ボタン等） */
  --danger: #dc2626;
}

/* 要素の幅計算を扱いやすくする共通設定 */
* {
  box-sizing: border-box;
}

/* ページ全体の基本見た目 */
body {
  margin: 0;
  font-family: "Segoe UI", sans-serif;
  background: var(--bg);
  color: var(--text);
}

/* コンテンツの最大幅と中央寄せ */
.container {
  max-width: 1080px;
  margin: 0 auto;
  padding: 24px;
}

header {
  margin-bottom: 12px;
}

h1 {
  margin: 0 0 4px;
}

h2 {
  margin: 0;
  font-size: 18px;
}

/* カード風の共通パネルスタイル */
.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 14px;
}

/* 横並び用の共通レイアウト */
.row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-row {
  margin: 10px 0 12px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

input,
select {
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 9px;
}

/* ボタン共通スタイル */
button {
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  color: #fff;
  background: var(--accent);
  cursor: pointer;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

button.delete {
  background: var(--danger);
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin: 10px 0 12px;
}

.status-grid .label {
  margin: 0 0 4px;
  color: var(--muted);
}

.status-grid .value {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

/* テーブルの基本設定 */
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th,
td {
  border-bottom: 1px solid var(--border);
  text-align: left;
  padding: 8px;
}

.muted {
  color: var(--muted);
}

/* 画面幅が狭い場合の表示調整 */
@media (max-width: 920px) {
  .status-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

/* 画面幅が狭い場合の表示調整 */
@media (max-width: 560px) {
  .status-grid {
    grid-template-columns: 1fr;
  }
}
```

---

## 6. `app.js` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/app.js`

```javascript
// HTMLの読み込み完了後に初期化処理を開始する
document.addEventListener("DOMContentLoaded", () => {
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const userSelect = document.getElementById("active-user-select");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const activeUserRole = document.getElementById("active-user-role");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const globalMessage = document.getElementById("global-message");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const todayDate = document.getElementById("today-date");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const todayStatus = document.getElementById("today-status");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const todayStart = document.getElementById("today-start");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const todayEnd = document.getElementById("today-end");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const clockInButton = document.getElementById("clock-in-btn");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const clockOutButton = document.getElementById("clock-out-btn");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const historyBody = document.getElementById("history-body");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const historyCount = document.getElementById("history-count");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const userBody = document.getElementById("user-body");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const userCount = document.getElementById("user-count");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const userSearchInput = document.getElementById("user-search-input");
  // HTML要素をIDで取得して、後続処理で使えるようにする
  const roleFilterSelect = document.getElementById("role-filter-select");

  // 要素取得に失敗した場合は安全に処理を中断する
  if (!(userSelect instanceof HTMLSelectElement) ||
      !(activeUserRole instanceof HTMLElement) ||
      !(globalMessage instanceof HTMLElement) ||
      !(todayDate instanceof HTMLElement) ||
      !(todayStatus instanceof HTMLElement) ||
      !(todayStart instanceof HTMLElement) ||
      !(todayEnd instanceof HTMLElement) ||
      !(clockInButton instanceof HTMLButtonElement) ||
      !(clockOutButton instanceof HTMLButtonElement) ||
      !(historyBody instanceof HTMLTableSectionElement) ||
      !(historyCount instanceof HTMLElement) ||
      !(userBody instanceof HTMLTableSectionElement) ||
      !(userCount instanceof HTMLElement) ||
      !(userSearchInput instanceof HTMLInputElement) ||
      !(roleFilterSelect instanceof HTMLSelectElement)) {
    return;
  }

  let users = [];
  let activeUserId = null;
  let activeTodayStatus = "NOT_STARTED";

  const setMessage = (text) => {
    globalMessage.textContent = text;
  };

  const safeText = (value) => {
    if (value == null || value === "") {
      return "-";
    }
    return value;
  };

  const parseActiveUserId = () => {
    const parsed = Number(userSelect.value);
    return Number.isInteger(parsed) ? parsed : null;
  };

  const refreshClockButtons = () => {
    clockInButton.disabled = activeUserId == null || activeTodayStatus !== "NOT_STARTED";
    clockOutButton.disabled = activeUserId == null || activeTodayStatus !== "WORKING";
  };

  const loadToday = async () => {
    if (activeUserId == null) {
      todayDate.textContent = "-";
      todayStatus.textContent = "-";
      todayStart.textContent = "-";
      todayEnd.textContent = "-";
      activeTodayStatus = "NOT_STARTED";
      refreshClockButtons();
      return;
    }
    // APIへHTTPリクエストを送信する
    const response = await fetch(`/api/attendance/today?userId=${activeUserId}`);
    // レスポンスJSONをJavaScriptオブジェクトへ変換する
    const data = await response.json();
    // HTTPステータスが失敗系ならエラーメッセージを扱う
    if (!response.ok) {
      throw new Error(data.error || "本日の勤怠取得に失敗しました。");
    }
    todayDate.textContent = data.date || "-";
    todayStatus.textContent = data.statusLabel || data.status;
    todayStart.textContent = safeText(data.startTime);
    todayEnd.textContent = safeText(data.endTime);
    activeTodayStatus = data.status || "NOT_STARTED";
    refreshClockButtons();
  };

  const loadHistory = async () => {
    if (activeUserId == null) {
      historyBody.innerHTML = `<tr><td colspan="4" class="muted">ユーザーがいません。</td></tr>`;
      historyCount.textContent = "件数: 0";
      return;
    }
    // APIへHTTPリクエストを送信する
    const response = await fetch(`/api/attendance/history?userId=${activeUserId}`);
    // レスポンスJSONをJavaScriptオブジェクトへ変換する
    const data = await response.json();
    // HTTPステータスが失敗系ならエラーメッセージを扱う
    if (!response.ok) {
      throw new Error(data.error || "履歴取得に失敗しました。");
    }
    historyCount.textContent = `件数: ${data.length}`;
    historyBody.innerHTML = "";
    if (data.length === 0) {
      historyBody.innerHTML = `<tr><td colspan="4" class="muted">履歴がありません。</td></tr>`;
      return;
    }
    data.forEach((record) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${record.date}</td>
        <td>${safeText(record.startTime)}</td>
        <td>${safeText(record.endTime)}</td>
        <td>${record.statusLabel}</td>
      `;
      historyBody.appendChild(row);
    });
  };

  const renderUserSelect = () => {
    const previous = activeUserId;
    userSelect.innerHTML = "";
    users.forEach((user) => {
      const option = document.createElement("option");
      option.value = String(user.id);
      option.textContent = `${user.username} (${user.role})`;
      userSelect.appendChild(option);
    });

    if (users.length === 0) {
      activeUserId = null;
      activeUserRole.textContent = "";
      refreshClockButtons();
      return;
    }

    const hasPrevious = users.some((user) => user.id === previous);
    activeUserId = hasPrevious ? previous : users[0].id;
    userSelect.value = String(activeUserId);
    const active = users.find((user) => user.id === activeUserId);
    activeUserRole.textContent = active ? `ロール: ${active.role}` : "";
  };

  const applyUserFilter = () => {
    const keyword = userSearchInput.value.trim().toLowerCase();
    const role = roleFilterSelect.value;

    const filtered = users.filter((user) => {
      const nameMatch = keyword === "" || user.username.toLowerCase().includes(keyword);
      const roleMatch = role === "" || user.role === role;
      return nameMatch && roleMatch;
    });

    userCount.textContent = `表示件数: ${filtered.length}件 / 全${users.length}件`;
    userBody.innerHTML = "";

    if (filtered.length === 0) {
      userBody.innerHTML = `<tr><td colspan="4" class="muted">条件に一致するユーザーがいません。</td></tr>`;
      return;
    }

    filtered.forEach((user) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${user.id}</td>
        <td>${escapeHtml(user.username)}</td>
        <td>${user.role}</td>
        <td><button type="button" class="delete">削除</button></td>
      `;

      const deleteButton = row.querySelector("button.delete");
      if (deleteButton instanceof HTMLButtonElement) {
        // ボタンクリック時の処理を登録する
        deleteButton.addEventListener("click", () => deleteUser(user));
      }
      userBody.appendChild(row);
    });
  };

  const loadUsers = async () => {
    // APIへHTTPリクエストを送信する
    const response = await fetch("/api/users");
    // レスポンスJSONをJavaScriptオブジェクトへ変換する
    const data = await response.json();
    // HTTPステータスが失敗系ならエラーメッセージを扱う
    if (!response.ok) {
      throw new Error(data.error || "ユーザー取得に失敗しました。");
    }
    users = data;
    renderUserSelect();
    applyUserFilter();
  };

  const postAttendance = async (endpoint) => {
    if (activeUserId == null) {
      return;
    }
    // APIへHTTPリクエストを送信する
    const response = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ userId: activeUserId })
    });
    // レスポンスJSONをJavaScriptオブジェクトへ変換する
    const data = await response.json();
    // HTTPステータスが失敗系ならエラーメッセージを扱う
    if (!response.ok) {
      throw new Error(data.error || "処理に失敗しました。");
    }
    setMessage(data.message || "更新しました。");
    await loadToday();
    await loadHistory();
  };

  const deleteUser = async (user) => {
    // ユーザーに最終確認ダイアログを表示する
    const ok = window.confirm(`ユーザー「${user.username}」を削除します。よろしいですか？`);
    if (!ok) {
      return;
    }

    // 通信成功時の処理
    try {
      // APIへHTTPリクエストを送信する
      const response = await fetch(`/api/users/${user.id}`, { method: "DELETE" });
      // レスポンスJSONをJavaScriptオブジェクトへ変換する
      const data = await response.json();
      // HTTPステータスが失敗系ならエラーメッセージを扱う
      if (!response.ok) {
        throw new Error(data.error || "削除に失敗しました。");
      }
      setMessage("ユーザーを削除しました。");
      await loadUsers();
      await loadToday();
      await loadHistory();
    // 通信失敗時の処理（ネットワークエラーなど）
    } catch (error) {
      setMessage(error.message || "削除に失敗しました。");
    }
  };

  // 選択値変更時に再描画/再取得する
  userSelect.addEventListener("change", async () => {
    activeUserId = parseActiveUserId();
    const active = users.find((user) => user.id === activeUserId);
    activeUserRole.textContent = active ? `ロール: ${active.role}` : "";
    await loadToday();
    await loadHistory();
  });

  // ボタンクリック時の処理を登録する
  clockInButton.addEventListener("click", async () => {
    // 通信成功時の処理
    try {
      await postAttendance("/api/attendance/clock-in");
    // 通信失敗時の処理（ネットワークエラーなど）
    } catch (error) {
      setMessage(error.message || "出勤に失敗しました。");
    }
  });

  // ボタンクリック時の処理を登録する
  clockOutButton.addEventListener("click", async () => {
    // 通信成功時の処理
    try {
      await postAttendance("/api/attendance/clock-out");
    // 通信失敗時の処理（ネットワークエラーなど）
    } catch (error) {
      setMessage(error.message || "退勤に失敗しました。");
    }
  });

  // 入力のたびに即時フィルタリングする
  userSearchInput.addEventListener("input", applyUserFilter);
  // 選択値変更時に再描画/再取得する
  roleFilterSelect.addEventListener("change", applyUserFilter);

  (async () => {
    // 通信成功時の処理
    try {
      await loadUsers();
      await loadToday();
      await loadHistory();
      setMessage("読み込み完了");
    // 通信失敗時の処理（ネットワークエラーなど）
    } catch (error) {
      setMessage(error.message || "初期化に失敗しました。");
    }
  })();
});

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");
}
```

---

## 7. コンパイル
Git Bash:

```bash
cd ~/order-management-springboot/practice/pre-springboot/step5-attendance-mini
javac -encoding UTF-8 App.java
```

---

## 8. 起動
Git Bash:

```bash
cd ~/order-management-springboot/practice/pre-springboot/step5-attendance-mini
java App
```

起動メッセージ:
- `Attendance Mini started: http://localhost:8094`

---

## 9. 画面確認（必須）
1. ブラウザで `http://localhost:8094` を開く
2. 対象ユーザーを選択し、当日状態が表示されることを確認
3. `出勤` → `退勤` を実行し、状態と履歴が更新されることを確認
4. ユーザー検索・ロール絞り込みが画面遷移なしで動くことを確認
5. ユーザー削除ボタンで確認ダイアログが表示されることを確認

---

## 10. 目的達成演習（必須）
1. 出勤/退勤の状態遷移がサーバー側で制御される流れを説明できる
2. 当日状態表示と履歴表示の連動処理を説明できる
3. ユーザー一覧の検索・絞り込みと削除確認分岐を説明できる
4. `enum` / `AtomicLong` / `synchronized` が必要な理由を説明できる

## 10.5 目的達成演習の具体手順
共通手順（各課題で共通）:
1. 該当ファイルを編集
2. コンパイル
   ```bash
   cd ~/order-management-springboot/practice/pre-springboot/step5-attendance-mini
   javac -encoding UTF-8 App.java
   ```
3. 起動（起動中なら `Ctrl + C` で再起動）
   ```bash
   cd ~/order-management-springboot/practice/pre-springboot/step5-attendance-mini
   java App
   ```
4. ブラウザで確認

### 1. 状態遷移がサーバー側で制御されることを確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/app.js`

`refreshClockButtons` を一時変更:
```javascript
clockInButton.disabled = false;
clockOutButton.disabled = false;
```

コード解説:
- フロント側ボタン制御を緩めても、最終判定はサーバー側の `clockIn` / `clockOut` が行う
- 状態遷移ルール（未出勤→出勤中→退勤済み）はサーバーで一元管理される

確認:
1. 同日に「出勤→出勤」や「未出勤で退勤」を試し、エラーになること
2. 確認後は `refreshClockButtons` の元の条件式に戻すこと

### 2. 当日表示と履歴表示の連動を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/app.js`

`postAttendance` 内を一時変更:
```javascript
setMessage(data.message || "更新しました。");
await loadToday();
// await loadHistory();
```

コード解説:
- 打刻後は `loadToday` と `loadHistory` の両方を更新して画面整合を保っている
- 片方を外すと、当日表示と履歴表示にズレが発生する

確認:
1. 打刻後に当日表示は更新されるが、履歴が更新されないこと
2. 確認後は `await loadHistory();` を戻すこと

### 3. 検索・絞り込みと削除確認分岐を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/app.js`

検索イベントを一時変更:
```javascript
userSearchInput.addEventListener("change", applyUserFilter);
```

`deleteUser` のキャンセル分岐を変更:
```javascript
if (!ok) {
  setMessage("ユーザー削除を中止しました。");
  return;
}
```

コード解説:
- `input` は入力中に即時反映、`change` は確定時のみ反映
- `deleteUser` の `return` は DELETE API を呼ばない分岐
- 検索・絞り込み・削除確認はいずれも画面遷移なしの UI 操作

確認:
1. 検索欄が即時反映されなくなることを確認後、`input` に戻すこと
2. 削除ダイアログでキャンセル時、DELETE リクエストが送信されないこと

### 4. 発展（任意）: 初期ユーザーを追加する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/App.java`

例:
```java
USER_STORE.create("yamada", "ROLE_USER");
```

### 5. 発展（任意）: `ROLE_ADMIN` 件数を表示する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step5-attendance-mini/static/app.js`

例:
```javascript
const adminCount = users.filter((user) => user.role === "ROLE_ADMIN").length;
userCount.textContent = `表示件数: ${filtered.length}件 / 全${users.length}件（ROLE_ADMIN: ${adminCount}件）`;
```

---

## 11. 理解ポイント
- 状態遷移（未出勤 → 出勤中 → 退勤済み）はサーバーで一元管理する
- クライアントはサーバー状態を取得してボタン活性/非活性を切り替える
- 検索・絞り込み・確認ダイアログは JavaScript で UX を改善できる

---

## 12. つまずきポイント
- 出勤/退勤ボタンが押せない:
  - `activeTodayStatus` とボタン制御ロジックを確認
- ユーザー削除できない:
  - 該当ユーザーに勤怠履歴があると `409` になる仕様
- 履歴が表示されない:
  - `userId` が正しくクエリに付与されているか確認












