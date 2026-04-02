# Lesson1 最小Webアプリ作成（Greeting Web）

## 目的（Lesson1でできるようになること）
- Java標準のHTTPサーバーでWebアプリを起動できる
- HTML/CSS/JavaScript と Java API の接続が分かる
- 画面入力を API に送信し、結果を画面表示できる

## 前提
- Day0基礎（HTML/CSS/Java）を実施済み
- Git Bash を使える
- JDK 17 がインストール済み

## Lesson1で作るもの
- 画面: 名前入力フォーム
- API: `POST /api/greeting`
- 動作: 入力した名前を使って「こんにちは、〇〇さん」を表示

---

## 0. 事前確認（Git Bashで実行）

```bash
java -version
javac -version
```

---

## 1. 作業フォルダ
作業場所（絶対パス）:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web`

Git Bash:
```bash
cd ~/order-management-springboot
mkdir -p practice/pre-springboot/step1-greeting-web/static
cd ~/order-management-springboot/practice/pre-springboot/step1-greeting-web
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
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/App.java`

```java
import com.sun.net.httpserver.HttpExchange; // HTTPリクエスト/レスポンス本体を扱うクラス
import com.sun.net.httpserver.HttpServer; // Java標準の簡易HTTPサーバー

import java.io.IOException; // 入出力エラー例外
import java.net.InetSocketAddress; // IPアドレス + ポートの組み合わせ
import java.nio.charset.StandardCharsets; // UTF-8などの文字コード定数
import java.nio.file.Files; // ファイル存在確認・読み込みに使用
import java.nio.file.Path; // ファイルパスを安全に扱う型
import java.util.regex.Matcher; // 正規表現の検索結果
import java.util.regex.Pattern; // 正規表現パターン

public class App {
    private static final int DEFAULT_PORT = 8090; // 引数がない場合の待受ポート
    private static final Path STATIC_DIR = Path.of("static"); // 画面ファイル（HTML/CSS/JS）置き場
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\""); // {"name":"..."} の name を抽出

    public static void main(String[] args) throws IOException {
        int port = resolvePort(args); // 起動引数からポート番号を決定

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); // 指定ポートでサーバー作成
        server.createContext("/", App::handleRoot); // / へのアクセス（トップ画面）
        // createContext(パス, 処理) で「そのURLが来た時の担当処理」を登録する
        // exchange は「今回1回分の通信情報」が入った箱（メソッド/URL/ヘッダー/本文/レスポンス書き込み先）
        // handleStatic(...) は共通メソッド。ここでは styles.css を返すように指定している
        server.createContext("/styles.css", exchange -> handleStatic(exchange, "styles.css", "text/css; charset=UTF-8")); // CSS配信
        server.createContext("/app.js", exchange -> handleStatic(exchange, "app.js", "application/javascript; charset=UTF-8")); // JS配信
        server.createContext("/api/greeting", App::handleGreetingApi); // Greeting API
        server.setExecutor(null); // 既定の実行方式（シンプル構成）
        server.start(); // サーバー起動

        System.out.println("Greeting Web started: http://localhost:" + port); // 起動確認メッセージ
    }

    private static int resolvePort(String[] args) {
        if (args.length == 0) { // 引数なしなら既定ポート
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(args[0]); // 引数が数値ならそのポートを使う
        } catch (NumberFormatException ex) {
            return DEFAULT_PORT; // 数値でなければ既定ポートへフォールバック
        }
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
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

    private static void handleGreetingApi(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) { // APIはPOSTのみ受け付け
            sendJson(exchange, 405, "{\"error\":\"Only POST is allowed\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // JSON文字列を取得
        String name = extractName(body).trim(); // JSONからnameを取り出して前後空白を除去
        if (name.isEmpty()) { // nameが空なら400エラー
            sendJson(exchange, 400, "{\"error\":\"name is required\"}");
            return;
        }

        String message = "こんにちは、" + name + "さん"; // 返却メッセージ生成
        String json = "{\"message\":\"" + escapeJson(message) + "\"}"; // JSON形式へ変換
        sendJson(exchange, 200, json); // 正常レスポンス
    }

    private static String extractName(String body) {
        Matcher matcher = NAME_PATTERN.matcher(body); // name抽出用正規表現を適用
        if (!matcher.find()) { // nameが見つからなければ空文字
            return "";
        }
        return matcher.group(1) // name値本体
            .replace("\\\"", "\"") // JSONエスケープを通常の"へ戻す
            .replace("\\\\", "\\") // \\ を \ へ戻す
            .replace("\\n", "\n") // 改行エスケープを改行文字へ戻す
            .replace("\\r", "\r") // CRエスケープを復元
            .replace("\\t", "\t"); // タブエスケープを復元
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}"); // 405ヘルパー
    }

    private static void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, "{\"error\":\"Not Found\"}"); // 404ヘルパー
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8); // JSON文字列をUTF-8バイト列へ変換
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8"); // JSONのMIMEタイプ
        exchange.sendResponseHeaders(status, body.length); // ステータスとボディ長を通知
        exchange.getResponseBody().write(body); // レスポンス本文を書き込む
        exchange.close(); // 必ずcloseしてレスポンス完了
    }

    private static String escapeJson(String value) {
        return value
            .replace("\\", "\\\\") // \ は最初にエスケープ
            .replace("\"", "\\\"") // " をエスケープ
            .replace("\n", "\\n") // 改行をエスケープ
            .replace("\r", "\\r") // CRをエスケープ
            .replace("\t", "\\t"); // タブをエスケープ
    }
}
```

---

## 4. `index.html` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/static/index.html`

```html
<!doctype html> <!-- HTML5文書であることを宣言 -->
<html lang="ja"> <!-- 文書言語を日本語に指定 -->
<head>
  <meta charset="utf-8"> <!-- 文字化け防止（UTF-8） -->
  <meta name="viewport" content="width=device-width, initial-scale=1"> <!-- スマホ表示対応 -->
  <title>Greeting Web</title> <!-- ブラウザタブに表示されるタイトル -->
  <link rel="stylesheet" href="/styles.css"> <!-- サーバーから配信されるCSSを読み込む -->
</head>
<body>
  <main class="container"> <!-- ページの中央寄せ用ラッパー -->
    <h1>Greeting Web</h1> <!-- 画面タイトル -->
    <p class="muted">Java + HTML/CSS/JavaScript の最小Webアプリ</p> <!-- 補足説明 -->

    <form id="greeting-form"> <!-- JSから取得するためにIDを付与 -->
      <label for="name">名前</label> <!-- 入力欄の説明 -->
      <input id="name" name="name" type="text" placeholder="例: Taro" required> <!-- 必須入力 -->
      <button type="submit">送信</button> <!-- 送信ボタン -->
    </form>

    <section class="panel"> <!-- 結果表示エリア -->
      <h2>結果</h2>
      <p id="result-message">ここにメッセージが表示されます。</p> <!-- JSで書き換える対象 -->
    </section>
  </main>
  <script src="/app.js" defer></script> <!-- HTML解析後にJSを実行 -->
</body>
</html>
```

---

## 5. `styles.css` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/static/styles.css`

```css
:root { /* 全体で使う色をCSS変数として定義 */
  --bg: #f6f7fb; /* ページ背景色 */
  --panel: #ffffff; /* パネル背景色 */
  --text: #1f2937; /* 基本文字色 */
  --muted: #6b7280; /* 補助文字色 */
  --border: #d1d5db; /* 枠線色 */
  --accent: #0ea5e9; /* ボタンの強調色 */
}

* {
  box-sizing: border-box; /* 幅計算にpadding/borderを含める */
}

body {
  margin: 0; /* 既定余白をリセット */
  background: var(--bg); /* 背景色を適用 */
  color: var(--text); /* 文字色を適用 */
  font-family: "Segoe UI", sans-serif; /* フォント指定 */
}

.container {
  max-width: 720px; /* 横幅上限 */
  margin: 0 auto; /* 横方向中央寄せ */
  padding: 24px; /* 内側余白 */
}

.muted {
  color: var(--muted); /* 補助テキスト色 */
}

form {
  display: grid; /* フォーム要素を縦並びにしやすくする */
  gap: 8px; /* 項目間余白 */
  background: var(--panel); /* フォーム背景 */
  border: 1px solid var(--border); /* 外枠線 */
  border-radius: 8px; /* 角丸 */
  padding: 16px; /* 内側余白 */
  margin-bottom: 16px; /* 下余白 */
}

input {
  padding: 10px; /* 入力しやすい高さを確保 */
  border: 1px solid var(--border); /* 枠線 */
  border-radius: 6px; /* 角丸 */
}

button {
  width: fit-content; /* ボタン幅を文字に合わせる */
  padding: 8px 14px; /* クリックしやすい余白 */
  border: none; /* 既定の枠線を消す */
  border-radius: 6px; /* 角丸 */
  color: white; /* 文字色 */
  background: var(--accent); /* 背景色 */
  cursor: pointer; /* ホバー時カーソルを手にする */
}

.panel {
  background: var(--panel); /* パネル背景色 */
  border: 1px solid var(--border); /* 枠線 */
  border-radius: 8px; /* 角丸 */
  padding: 16px; /* 内側余白 */
}
```

---

## 6. `app.js` を作成
作成ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/static/app.js`

```javascript
document.addEventListener("DOMContentLoaded", () => { // HTML読込完了後に処理を開始
  const form = document.getElementById("greeting-form"); // フォーム要素を取得
  const nameInput = document.getElementById("name"); // 名前入力欄を取得
  const resultMessage = document.getElementById("result-message"); // 結果表示要素を取得

  // 要素が想定どおり取得できたかを型込みでチェック
  if (!(form instanceof HTMLFormElement) ||
      !(nameInput instanceof HTMLInputElement) ||
      !(resultMessage instanceof HTMLElement)) {
    return; // 取得失敗時は安全に処理終了
  }

  form.addEventListener("submit", async (event) => { // フォーム送信イベントを監視
    event.preventDefault(); // ブラウザ既定の画面遷移を止める

    const name = nameInput.value.trim(); // 入力値の前後空白を除去
    if (name.length === 0) { // 空文字は送信しない
      resultMessage.textContent = "名前を入力してください。";
      return;
    }

    resultMessage.textContent = "送信中..."; // 通信中メッセージを先に表示

    try {
      const response = await fetch("/api/greeting", { // Greeting APIへPOST
        method: "POST",
        headers: {
          "Content-Type": "application/json" // JSON送信を宣言
        },
        body: JSON.stringify({ name }) // {name: "..."} をJSON文字列化
      });

      const data = await response.json(); // レスポンスJSONをオブジェクト化
      if (!response.ok) { // HTTPエラー（400/405など）の場合
        resultMessage.textContent = data.error || "エラーが発生しました。";
        return;
      }

      resultMessage.textContent = data.message; // 正常時メッセージを表示
    } catch (error) { // ネットワーク障害などfetch失敗時
      resultMessage.textContent = "通信に失敗しました。";
    }
  });
});
```

---

## 7. コンパイル
Git Bash:

```bash
cd ~/order-management-springboot/practice/pre-springboot/step1-greeting-web
javac -encoding UTF-8 App.java
```

---

## 8. 起動
Git Bash:

```bash
cd ~/order-management-springboot/practice/pre-springboot/step1-greeting-web
java App
```

起動メッセージ:
- `Greeting Web started: http://localhost:8090`

---

## 9. 画面確認（必須）
1. ブラウザで `http://localhost:8090` を開く
2. 名前に `Taro` と入力して送信
3. 「こんにちは、Taroさん」と表示されることを確認
4. 空入力のときはエラーメッセージが表示されることを確認

---

## 10. 目的達成演習（必須）
1. Java標準HTTPサーバーを起動し、ポート変更の仕組みを説明できる
2. HTML/CSS/JavaScript と Java API の接続箇所を説明できる
3. 画面入力が API を通って結果表示される流れを説明できる

## 10.5 目的達成演習の具体手順
共通手順（各課題で共通）:
1. 該当ファイルを編集
2. コンパイル
   ```bash
   cd ~/order-management-springboot/practice/pre-springboot/step1-greeting-web
   javac -encoding UTF-8 App.java
   ```
3. 起動（起動中なら `Ctrl + C` で再起動）
   ```bash
   cd ~/order-management-springboot/practice/pre-springboot/step1-greeting-web
   java App
   ```
4. ブラウザで確認
5. 一時変更の課題は必ず元のコードに戻す

### 1. サーバー起動とポート制御を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/App.java`

変更前:
```java
private static final int DEFAULT_PORT = 8090;
```

変更後:
```java
private static final int DEFAULT_PORT = 8100;
```

確認:
1. `java App` で `http://localhost:8100` が開けること
2. `java App 8200` で `http://localhost:8200` が開けること

コード解説:
- `DEFAULT_PORT` は引数未指定時の待受ポート
- `resolvePort` が引数優先で最終的なポートを決める

### 2. 画面と API の接続箇所を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/static/app.js`

`fetch` のURLを一時変更:
```javascript
const response = await fetch("/api/greeting-x", {
```

確認:
1. 送信時に失敗メッセージになること
2. URLを `/api/greeting` に戻すと成功すること

コード解説:
- `app.js` の `fetch("/api/greeting", ...)` が API 呼び出しの接続点
- Java側は `server.createContext("/api/greeting", App::handleGreetingApi);` で受け口を作っている
- フロントURLとサーバールーティングが一致しないと接続できない

### 3. 入力値が画面へ戻る流れを確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/App.java`

変更前:
```java
String message = "こんにちは、" + name + "さん";
```

変更後:
```java
String message = "[API応答] こんにちは、" + name + "さん";
```

確認:
1. 画面送信後の表示が `[API応答] こんにちは、〇〇さん` になること
2. 変更を元に戻すこと

コード解説:
- `nameInput.value`（画面入力）が `fetch` で API へ送られる
- `handleGreetingApi` が `message` をJSONで返す
- `resultMessage.textContent = data.message;` で画面へ反映される

### 4. 発展（任意）: 入力の前後空白処理を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/App.java`

一時変更:
```java
String name = extractName(body);
```

確認:
1. 空白のみ入力時の挙動を確認する
2. 確認後は `.trim()` ありに戻す

### 5. 発展（任意）: JSONエスケープの必要性を確認する
編集ファイル:
- `~/order-management-springboot/practice/pre-springboot/step1-greeting-web/App.java`

一時変更:
```java
String json = "{\"message\":\"" + message + "\"}";
```

確認:
1. `"` や `\\` を含む入力で挙動差を確認する
2. 確認後は `escapeJson(message)` に戻す

---

## 11. 理解ポイント
- 画面（HTML）→ JavaScript → API（Java）の接続が最小構成で体験できる
- API との通信は `fetch` と JSON で行う
- 画面表示と API 処理を分けることで見通しが良くなる

---

## 12. つまずきポイント
- 日本語が文字化けする:
  - `javac -encoding UTF-8 App.java` でコンパイルする
- `404 Not Found`:
  - `static/index.html`, `static/styles.css`, `static/app.js` の配置を確認
- 送信しても表示されない:
  - ブラウザの DevTools（Console / Network）でエラー確認
