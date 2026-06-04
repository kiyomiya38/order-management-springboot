import com.sun.net.httpserver.HttpExchange; // HTTPリクエスト/レスポンス本体を扱うクラス
import com.sun.net.httpserver.HttpServer; // Java標準の簡易HTTPサーバー

import java.io.IOException; // 入出力エラー例外
import java.net.InetSocketAddress; // IPアドレス + ポートの組み合わせ
import java.nio.charset.StandardCharsets; // UTF-8などの文字コード定数
import java.nio.file.Files; // ファイル存在確認・読み込みに使用
import java.nio.file.Path; // ファイルパスを安全に扱う型
import java.util.ArrayList; // 可変長リストの代表実装
import java.util.List; // リスト型のインターフェース
import java.util.concurrent.atomic.AtomicLong; // 同時アクセスでも安全に連番を増やすクラス
import java.util.regex.Matcher; // 正規表現の検索結果
import java.util.regex.Pattern; // 正規表現パターン

public class App { // Lesson2で作るWebアプリ本体
    private static final int DEFAULT_PORT = 8089; // Lesson2用の待受ポート
    private static final Path STATIC_DIR = Path.of("static"); // 画面ファイル置き場
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\""); // {"name":"..."} の name を抽出
    private static final MessageStore STORE = new MessageStore(); // メモリ上のメッセージ保存先

    // Javaアプリのエントリーポイント（JVMが最初に呼ぶメソッド）
    // public: 外部（JVM）から呼び出せるようにする
    // static: Appのインスタンス生成なしで呼び出せるようにする
    // void: 戻り値なし / String[] args: 起動引数（例: 8089）
    // throws IOException: ファイル・通信などの入出力エラーを呼び出し元へ伝える
    public static void main(String[] args) throws IOException {
        int port = resolvePort(args); // 引数があれば引数、なければDEFAULT_PORT

        // localhost:port で待ち受けるHTTPサーバーを生成
        // 第2引数の 0 は backlog（同時接続待ちキュー長）を OS 既定値に任せる指定
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); // HTTPサーバー作成
        server.createContext("/", App::handleRoot); // / へのアクセス（トップ画面）
        // createContext(パス, 処理) で「そのURLが来た時の担当処理」を登録する
        // exchange は「今回1回分の通信情報」が入った箱（メソッド/URL/ヘッダー/本文/レスポンス書き込み先）
        // handleStatic(...) は共通メソッド。ここでは styles.css を返すように指定している
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8")); // CSS
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "text/javascript; charset=UTF-8")); // JavaScript
        server.createContext("/api/health", App::handleHealth); // APIの起動状態確認
        server.createContext("/api/messages", App::handleMessages); // メッセージ一覧/登録
        server.setExecutor(null); // 既定の実行方式（シンプル構成）
        server.start(); // 待受開始

        System.out.println("started: http://localhost:" + port); // 起動確認メッセージ
    }

    private static int resolvePort(String[] args) { // 起動引数からポート番号を決める
        if (args.length == 0) { // 引数なしなら既定ポート
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(args[0]); // 引数が数値ならそのポートを使う
        } catch (NumberFormatException e) { // 数値に変換できない場合
            return DEFAULT_PORT; // 既定ポートへフォールバック
        }
    }

    private static void handleRoot(HttpExchange exchange) throws IOException { // / へのアクセスを処理する
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) { // GET以外は拒否
            sendMethodNotAllowed(exchange);
            return;
        }

        if (!"/".equals(exchange.getRequestURI().getPath())) { // / 以外のパスは404
            sendNotFound(exchange);
            return;
        }

        handleStatic(exchange, "index.html", "text/html; charset=UTF-8"); // トップ画面HTMLを返す
    }

    // 共通メソッド: 指定された fileName を static 配下から読み込み、contentType で返す
    // exchange: 今回の通信情報（リクエスト情報 + レスポンス出力先）
    // fileName: 返す実ファイル名（例: styles.css）
    // contentType: 返すデータ種別（例: text/css; charset=UTF-8）
    private static void handleStatic(HttpExchange exchange, String fileName, String contentType) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) { // 静的ファイルもGETのみ許可
            sendMethodNotAllowed(exchange);
            return;
        }

        Path file = STATIC_DIR.resolve(fileName); // static配下の対象ファイルを指すPathを作る
        if (!Files.exists(file)) { // ファイルがなければ404
            sendNotFound(exchange);
            return;
        }

        byte[] body = Files.readAllBytes(file); // ファイルをバイト配列で読み込み
        exchange.getResponseHeaders().set("Content-Type", contentType); // Content-Type設定
        exchange.sendResponseHeaders(200, body.length); // HTTP 200 + ボディ長を返す
        exchange.getResponseBody().write(body); // レスポンスボディへ書き込み
        exchange.close(); // レスポンスを閉じて完了
    }

    private static void handleHealth(HttpExchange exchange) throws IOException { // /api/health を処理する
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) { // API状態確認はGETのみ許可
            sendMethodNotAllowed(exchange);
            return;
        }

        ApiStatus status = ApiStatus.OK; // enumで固定値OKを表す
        sendJson(exchange, 200, "{\"status\":\"" + status + "\",\"message\":\"ready\"}"); // API稼働中をJSONで返す
    }

    private static void handleMessages(HttpExchange exchange) throws IOException { // /api/messages のGET/POSTを処理する
        String method = exchange.getRequestMethod(); // GET / POST などのHTTPメソッドを取得

        if ("GET".equalsIgnoreCase(method)) { // GETなら一覧取得
            List<Message> messages = STORE.list(); // 保存済みメッセージを取得
            sendJson(exchange, 200, toMessageListJson(messages)); // JSON配列として返す
            return;
        }

        if ("POST".equalsIgnoreCase(method)) { // POSTなら新規登録
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // リクエスト本文(JSON文字列)をUTF-8で取得
            String name = extractName(body).trim(); // JSONからnameを取り出して前後空白を除去

            if (name.isEmpty()) { // nameが空なら入力エラー
                sendJson(exchange, 400, "{\"error\":\"name is required\"}"); // HTTP 400でエラーJSONを返す
                return;
            }

            Message message = STORE.create(name); // メモリ上にメッセージを保存
            sendJson(exchange, 201, toMessageJson(message, ApiStatus.CREATED)); // HTTP 201で作成結果を返す
            return;
        }

        sendMethodNotAllowed(exchange); // GET/POST以外は405
    }

    private static String extractName(String body) { // JSON文字列からname値を取り出す
        Matcher matcher = NAME_PATTERN.matcher(body); // name抽出用正規表現を適用
        if (!matcher.find()) { // nameが見つからなければ空文字
            return "";
        }

        return unescapeJson(matcher.group(1)); // 1番目のキャプチャグループ（name値）を復元して返す
    }

    private static String toMessageListJson(List<Message> messages) { // メッセージ一覧をJSON配列文字列へ変換する
        StringBuilder builder = new StringBuilder(); // 文字列連結を効率よく行うための入れ物
        builder.append("["); // JSON配列の開始

        for (int i = 0; i < messages.size(); i++) { // 一覧を先頭から順に処理
            if (i > 0) { // 2件目以降は要素区切りのカンマを入れる
                builder.append(",");
            }

            Message message = messages.get(i); // i番目のMessageを取得
            builder.append("{") // JSONオブジェクトの開始
                .append("\"id\":").append(message.id()).append(",") // idは数値として出力
                .append("\"name\":\"").append(escapeJson(message.name())).append("\",") // nameは文字列なのでエスケープして出力
                .append("\"text\":\"").append(escapeJson(message.text())).append("\"") // textも文字列として出力
                .append("}"); // JSONオブジェクトの終了
        }

        builder.append("]"); // JSON配列の終了
        return builder.toString(); // StringBuilderの内容をStringにして返す
    }

    private static String toMessageJson(Message message, ApiStatus status) { // 登録結果1件をJSON文字列へ変換する
        return "{" // JSONオブジェクトの開始
            + "\"status\":\"" + status + "\"," // 処理結果（CREATEDなど）
            + "\"id\":" + message.id() + "," // 採番されたID
            + "\"name\":\"" + escapeJson(message.name()) + "\"," // 入力された名前
            + "\"message\":\"" + escapeJson(message.text()) + "\"" // 画面表示用メッセージ
            + "}"; // JSONオブジェクトの終了
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException { // 405を返す共通処理
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}"); // HTTPメソッド違反
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException { // 404を返す共通処理
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}"); // URLやファイルが見つからない
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException { // JSONレスポンスの共通送信処理
        byte[] body = json.getBytes(StandardCharsets.UTF_8); // JSON文字列をUTF-8バイト列へ変換
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8"); // JSONのMIMEタイプ
        exchange.sendResponseHeaders(status, body.length); // ステータスコードとボディ長を通知
        exchange.getResponseBody().write(body); // レスポンス本文を書き込む
        exchange.close(); // 必ずcloseしてレスポンス完了
    }

    private static String escapeJson(String value) { // JSON文字列として安全になるように特殊文字をエスケープする
        return value
            .replace("\\", "\\\\") // \ は最初にエスケープ
            .replace("\"", "\\\"") // " をエスケープ
            .replace("\n", "\\n") // 改行をエスケープ
            .replace("\r", "\\r") // CRをエスケープ
            .replace("\t", "\\t"); // タブをエスケープ
    }

    private static String unescapeJson(String value) { // JSON文字列内のエスケープを通常文字へ戻す
        return value
            .replace("\\\"", "\"") // \" を " へ戻す
            .replace("\\\\", "\\") // \\ を \ へ戻す
            .replace("\\n", "\n") // \n を改行文字へ戻す
            .replace("\\r", "\r") // \r をCRへ戻す
            .replace("\\t", "\t"); // \t をタブへ戻す
    }

    private enum ApiStatus { // APIの処理状態を固定候補で表す
        OK, // 正常に取得できた状態
        CREATED // 新規作成できた状態
    }

    private record Message(long id, String name, String text) { // メッセージ1件分のデータ
    }

    private static final class MessageStore { // メモリ上でメッセージを管理するクラス
        private final AtomicLong sequence = new AtomicLong(0); // ID採番用カウンタ
        private final List<Message> messages = new ArrayList<>(); // 保存済みメッセージ一覧

        public synchronized List<Message> list() { // 一覧取得。synchronizedで読み取り中の競合を防ぐ
            return new ArrayList<>(messages); // 内部リストを直接渡さずコピーを返す
        }

        public synchronized Message create(String name) { // 新規作成。synchronizedで同時登録時の競合を防ぐ
            String text = "こんにちは、" + name + "さん"; // 保存するメッセージ本文を作成
            Message message = new Message(sequence.incrementAndGet(), name, text); // IDを1つ進めてMessageを生成
            messages.add(message); // メモリ上の一覧へ追加
            return message; // 作成したデータを呼び出し元へ返す
        }
    }
}
