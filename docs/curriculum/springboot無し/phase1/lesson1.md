# Lesson1: 最初のWeb画面を作る（分割ファイル構成）

## ロードマップ接続
- 対象: `springless-roadmap.md` の Phase1
- このLessonで行うこと: 完成版に近い見た目を先に作り、機能は後続Lessonで追加する
- 重要方針: **Lesson1の時点からファイル分割**して進める（途中で構成変更しない）

## このLessonのゴール
- `http://localhost:8080/`（トップ）
- `http://localhost:8080/health`（監視用）
- `http://localhost:8080/login`
- `http://localhost:8080/attendances`
- `http://localhost:8080/users`
- `http://localhost:8080/admin/attendances`

上記6画面が表示できる状態にする（表示値は固定値でOK）。

---

## 1. 作業フォルダ
```bash
cd ~/order-management-springboot
mkdir -p ~/order-management-springboot/practice/springless-final-web
cd ~/order-management-springboot/practice/springless-final-web
```

---

## 2. Lesson1で使うファイル（最初から分割）
- `App.java`
  - サーバー起動
  - ルーティング（URLと処理の対応）
- `PageHandlers.java`
  - URLごとの処理（`handleTop` など）
- `HtmlLayout.java`
  - 各画面のHTML文字列
  - 共通の `wrapHtml` と CSS
- `HttpResponses.java`
  - `sendHtml` / `sendText` の共通送信処理

---

## 3. Step1: トップ画面（`/`）とヘルスチェック（`/health`）を作る

### このStepで学ぶ文法
- `class` と `main` メソッド
- メソッド分割
- `if` によるHTTPメソッド判定
- `try-with-resources`

### 3-1. `App.java` を作成
```java
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    // Javaアプリの開始地点。サーバー起動はここから始まる。
    public static void main(String[] args) throws IOException {
        // 8080番ポートでHTTPサーバーを作成する。
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // URLと処理メソッドを対応付ける（ルーティング）。
        server.createContext("/", PageHandlers::handleTop);
        server.createContext("/health", PageHandlers::handleHealth);

        // Step2以降で1つずつ有効化する。
        // server.createContext("/login", PageHandlers::handleLogin);
        // server.createContext("/attendances", PageHandlers::handleAttendances);
        // server.createContext("/users", PageHandlers::handleUsers);
        // server.createContext("/admin/attendances", PageHandlers::handleAdminAttendances);

        // リクエスト処理方式。nullはデフォルト設定。
        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
    }
}
```

### 3-2. `PageHandlers.java` を作成
```java
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PageHandlers {
    // インスタンス化しないユーティリティクラス。
    private PageHandlers() {
    }

    // トップ画面（/）
    public static void handleTop(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendHtml(exchange, 200, HtmlLayout.topPageHtml());
    }

    // 監視確認（/health）
    public static void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpResponses.sendText(exchange, 405, "Method Not Allowed");
            return;
        }

        HttpResponses.sendText(exchange, 200, "OK");
    }
}
```

### 3-3. `HtmlLayout.java` を作成
```java
public class HtmlLayout {
    private HtmlLayout() {
    }

    // トップ画面のHTML（固定値表示）
    public static String topPageHtml() {
        String body = """
                <header>
                  <h1>勤怠管理システム（MVP）</h1>
                  <p class="subtitle">研修用 / ログインあり</p>
                  <div class="row">
                    <span class="muted">ログイン中: <strong>user1</strong></span>
                    <a href="/attendances">勤怠一覧</a>
                    <a href="/users">アカウント管理</a>
                    <a href="/admin/attendances">勤怠管理</a>
                    <form method="post" action="/logout">
                      <button type="submit" class="danger">ログアウト</button>
                    </form>
                  </div>
                </header>

                <div class="alert alert-info">メッセージ表示エリア（機能は後続Lessonで実装）</div>

                <section class="panel">
                  <div class="panel-header">
                    <h2>今日の勤怠</h2>
                    <span class="status-badge">未出勤</span>
                  </div>
                  <p>日付: 2026-03-26</p>
                  <p>出勤時刻: -</p>
                  <p>退勤時刻: -</p>
                  <div class="row">
                    <form method="post" action="/clock-in">
                      <button type="submit">出勤</button>
                    </form>
                    <form method="post" action="/clock-out">
                      <button type="submit">退勤</button>
                    </form>
                  </div>
                </section>

                <section class="panel">
                  <h2>業務ルール（抜粋）</h2>
                  <ul>
                    <li>同日に複数回の出勤は不可</li>
                    <li>未出勤で退勤は不可</li>
                    <li>退勤後に再度退勤は不可</li>
                  </ul>
                </section>
                """;

        return wrapHtml("勤怠管理（MVP）", body);
    }

    // 共通のHTML枠
    public static String wrapHtml(String title, String bodyContent) {
        return """
                <!doctype html>
                <html lang="ja">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>%s</title>
                  <style>%s</style>
                </head>
                <body>
                  <div class="container">
                    %s
                  </div>
                </body>
                </html>
                """.formatted(title, STYLE_CSS, bodyContent);
    }

    // Lesson1用の共通スタイル
    private static final String STYLE_CSS = """
            :root {
              --bg: #f6f6f2;
              --panel: #ffffff;
              --text: #202124;
              --muted: #6b7280;
              --accent: #0ea5e9;
              --border: #e5e7eb;
            }

            * { box-sizing: border-box; }

            body {
              margin: 0;
              font-family: "Segoe UI", Tahoma, sans-serif;
              color: var(--text);
              background: var(--bg);
            }

            .container {
              max-width: 920px;
              margin: 0 auto;
              padding: 24px;
            }

            header { margin-bottom: 16px; }
            h1 { margin: 0 0 4px; }

            .subtitle {
              color: var(--muted);
              margin: 0 0 16px;
            }

            .panel {
              background: var(--panel);
              border: 1px solid var(--border);
              border-radius: 8px;
              padding: 16px;
              margin-bottom: 16px;
            }

            .panel-header {
              display: flex;
              align-items: center;
              justify-content: space-between;
            }

            .status-badge {
              display: inline-block;
              padding: 4px 10px;
              border-radius: 999px;
              background: #e0f2fe;
              color: #0369a1;
              font-size: 12px;
            }

            .row {
              display: flex;
              gap: 8px;
              flex-wrap: wrap;
              align-items: center;
            }

            input, select {
              padding: 8px;
              border: 1px solid var(--border);
              border-radius: 6px;
            }

            button {
              padding: 8px 12px;
              background: var(--accent);
              color: #fff;
              border: none;
              border-radius: 6px;
              cursor: pointer;
            }

            button:hover { opacity: 0.9; }
            .danger { background: #ef4444; }
            .muted { color: var(--muted); }

            table {
              width: 100%;
              border-collapse: collapse;
              font-size: 14px;
            }

            th, td {
              border-bottom: 1px solid var(--border);
              text-align: left;
              padding: 8px;
            }

            .alert {
              padding: 10px 12px;
              border-radius: 6px;
              margin-bottom: 12px;
            }

            .alert-info {
              background: #e0f2fe;
              color: #075985;
              border: 1px solid #bae6fd;
            }
            """;
}
```

### 3-4. `HttpResponses.java` を作成
```java
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponses {
    private HttpResponses() {
    }

    // HTMLレスポンス送信の共通処理
    public static void sendHtml(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        // try-with-resources: ブロックを抜けると自動でcloseされる。
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // テキストレスポンス送信の共通処理
    public static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        // try-with-resources: close忘れ防止。
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
```

### 3-5. 実行
```bash
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java
java App
```

確認:
- `http://localhost:8080/` が開く
- `http://localhost:8080/health` が `OK`

---

## 4. Step2: ログイン画面（`/login`）を追加する

### 4-1. `App.java` でルーティングを有効化
変更箇所:
- `main` のコメントアウト行

```java
server.createContext("/login", PageHandlers::handleLogin);
```

### 4-2. `PageHandlers.java` にメソッド追加
追加場所:
- `handleHealth` の下

```java
public static void handleLogin(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.loginPageHtml());
}
```

### 4-3. `HtmlLayout.java` にメソッド追加
追加場所:
- `topPageHtml` の下（`wrapHtml` の前）

```java
public static String loginPageHtml() {
    String body = """
            <header>
              <h1>勤怠管理システム（MVP）</h1>
              <p class="subtitle">ログインしてください</p>
              <a href="/">トップへ戻る</a>
            </header>

            <section class="panel">
              <form method="post" action="/login">
                <div class="row">
                  <label>ユーザー名
                    <input type="text" name="username" required />
                  </label>
                  <label>パスワード
                    <input type="password" name="password" required />
                  </label>
                </div>
                <button type="submit">ログイン</button>
              </form>
              <p class="muted">初期ユーザー: admin / admin123, user1 / password</p>
            </section>
            """;

    return wrapHtml("ログイン", body);
}
```

### 4-4. 反映
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java
java App
```

確認:
- `http://localhost:8080/login` が開く

---

## 5. Step3: 勤怠一覧画面（`/attendances`）を追加する

### 5-1. `App.java` でルーティングを有効化
```java
server.createContext("/attendances", PageHandlers::handleAttendances);
```

### 5-2. `PageHandlers.java` にメソッド追加
追加場所:
- `handleLogin` の下

```java
public static void handleAttendances(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.attendancesPageHtml());
}
```

### 5-3. `HtmlLayout.java` にメソッド追加
追加場所:
- `loginPageHtml` の下（`wrapHtml` の前）

```java
public static String attendancesPageHtml() {
    String body = """
            <header>
              <h1>勤怠一覧</h1>
              <p class="subtitle">user1 の履歴（降順）</p>
              <a href="/">トップへ戻る</a>
            </header>

            <section class="panel">
              <table>
                <thead>
                  <tr>
                    <th>日付</th>
                    <th>出勤時刻</th>
                    <th>退勤時刻</th>
                    <th>状態</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2026-03-26</td>
                    <td>-</td>
                    <td>-</td>
                    <td>未出勤</td>
                  </tr>
                </tbody>
              </table>
            </section>
            """;

    return wrapHtml("勤怠一覧", body);
}
```

### 5-4. 反映
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java
java App
```

確認:
- `http://localhost:8080/attendances` が開く

---

## 6. Step4: アカウント管理画面（`/users`）を追加する

### 6-1. `App.java` でルーティングを有効化
```java
server.createContext("/users", PageHandlers::handleUsers);
```

### 6-2. `PageHandlers.java` にメソッド追加
追加場所:
- `handleAttendances` の下

```java
public static void handleUsers(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.usersPageHtml());
}
```

### 6-3. `HtmlLayout.java` にメソッド追加
追加場所:
- `attendancesPageHtml` の下（`wrapHtml` の前）

```java
public static String usersPageHtml() {
    String body = """
            <header>
              <h1>アカウント管理</h1>
              <div class="row">
                <a href="/">トップへ戻る</a>
                <a href="/users/new">新規作成</a>
              </div>
            </header>

            <section class="panel">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>ユーザー名</th>
                    <th>ロール</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>1</td>
                    <td>user1</td>
                    <td>ROLE_USER</td>
                    <td>編集 / 削除</td>
                  </tr>
                  <tr>
                    <td>2</td>
                    <td>admin</td>
                    <td>ROLE_ADMIN</td>
                    <td>編集 / 削除</td>
                  </tr>
                </tbody>
              </table>
            </section>
            """;

    return wrapHtml("アカウント管理", body);
}
```

### 6-4. 反映
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java
java App
```

確認:
- `http://localhost:8080/users` が開く

---

## 7. Step5: 管理者勤怠管理画面（`/admin/attendances`）を追加する

### 7-1. `App.java` でルーティングを有効化
```java
server.createContext("/admin/attendances", PageHandlers::handleAdminAttendances);
```

### 7-2. `PageHandlers.java` にメソッド追加
追加場所:
- `handleUsers` の下

```java
public static void handleAdminAttendances(HttpExchange exchange) throws IOException {
    if (!"GET".equals(exchange.getRequestMethod())) {
        HttpResponses.sendText(exchange, 405, "Method Not Allowed");
        return;
    }

    HttpResponses.sendHtml(exchange, 200, HtmlLayout.adminAttendancesPageHtml());
}
```

### 7-3. `HtmlLayout.java` にメソッド追加
追加場所:
- `usersPageHtml` の下（`wrapHtml` の前）

```java
public static String adminAttendancesPageHtml() {
    String body = """
            <header>
              <h1>勤怠管理（管理者）</h1>
              <div class="row">
                <a href="/">トップへ戻る</a>
                <a href="/users">アカウント管理</a>
              </div>
            </header>

            <section class="panel">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>ユーザー名</th>
                    <th>日付</th>
                    <th>出勤時刻</th>
                    <th>退勤時刻</th>
                    <th>状態</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>1</td>
                    <td>user1</td>
                    <td>2026-03-26</td>
                    <td>-</td>
                    <td>-</td>
                    <td>未出勤</td>
                    <td>編集</td>
                  </tr>
                </tbody>
              </table>
            </section>
            """;

    return wrapHtml("勤怠管理（管理者）", body);
}
```

### 7-4. 反映
```bash
Ctrl + C
javac -encoding UTF-8 App.java PageHandlers.java HtmlLayout.java HttpResponses.java
java App
```

確認:
- `http://localhost:8080/admin/attendances` が開く

---

## 8. Step1コードのメソッド説明（定義元 + 入力・処理・出力）

1. `main(String[] args) throws IOException`
- 定義元: 自作（`App`）
- 入力: `args`
- 処理: サーバー生成 -> URL紐付け -> 起動
- 出力: `localhost:8080` で待受開始

2. `server.createContext(path, handler)`
- 定義元: `HttpServer`（JDK）
- 入力: URL文字列, 処理メソッド
- 処理: URLが来た時に呼ぶメソッドを登録
- 出力: ルーティング定義が増える

3. `handleTop(HttpExchange exchange)`
- 定義元: 自作（`PageHandlers`）
- 入力: `exchange`（1リクエスト分の情報）
- 処理: GET判定 -> HTML生成メソッド呼び出し -> レスポンス送信
- 出力: トップ画面（200）

4. `topPageHtml()`
- 定義元: 自作（`HtmlLayout`）
- 入力: なし
- 処理: 本文HTML + 共通外枠を組み立て
- 出力: 完成済みHTML文字列

5. `sendHtml(exchange, status, body)`
- 定義元: 自作（`HttpResponses`）
- 入力: `exchange`, `status`, `body`
- 処理: UTF-8変換 -> ヘッダ設定 -> 本文送信
- 出力: HTMLレスポンス

6. `try (OutputStream os = exchange.getResponseBody()) { ... }`
- 定義元: Java文法（try-with-resources）
- 入力: 自動close対象のリソース（`os`）
- 処理: ブロック内で書き込み、終了時に自動close
- 出力: close忘れを防いだ安全な送信

---

## 9. 最終確認（Lesson1完了条件）
- `/` `/health` `/login` `/attendances` `/users` `/admin/attendances` が表示できる
- `App.java` は「起動とルーティング」だけに集中している
- 画面HTMLは `HtmlLayout.java` に集約されている
- 送信処理は `HttpResponses.java` に集約されている

## 10. 次Lessonへの引き継ぎ
- Lesson2では、この分割構成のまま `User` / `Attendance` クラスを追加し、固定値をオブジェクト値に置き換える
