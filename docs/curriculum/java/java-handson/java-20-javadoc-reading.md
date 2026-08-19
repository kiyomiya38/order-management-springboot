# Java-20 ハンズオン: Oracle Javadocの読み方（実装付き）

## 1. この資料のゴール
- Oracle Javadoc を参照して API 仕様を確認できる
- 確認した仕様どおりに Java コードを実装できる
- 実行結果で「仕様理解が正しいか」を検証できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

参照先（Oracle公式のみ）:
- https://docs.oracle.com/javase/jp/17/docs/api/index.html

---

## 3. 先に覚えるポイント
1. Java API は「Javaが用意している機能の使い方ルール（メソッド名・引数・戻り値・例外）」のこと。APIを呼ぶ側は、このルールどおりに使う
2. API のクラスを使うときは、基本的に `import` が必要（例: `import java.util.List;`）
3. `import` が不要な主な例は `java.lang`（`String` など）、同じパッケージのクラス、完全修飾名で書く場合（例: `java.util.List`）
4. 先に `Method Detail` を読み、`Parameters` / `Returns` / `Throws` を確認する
5. 仕様を読んだら、最小コードで即検証する
6. 推測で書かず、「Javadocに書いてあること」だけを根拠に実装する

### メソッドの見出しを分解して読む

たとえば、次のようなメソッド表記を見つけたとします。

```java
String substring(int beginIndex, int endIndex)
```

| 部分 | 意味 |
| --- | --- |
| 最初の`String` | 呼び出した結果として返る型 |
| `substring` | メソッド名 |
| `int beginIndex` | 1つ目に渡す値とその型 |
| `int endIndex` | 2つ目に渡す値とその型 |

読み取った書式をコードへ対応させると、次のようになります。

```java
String result = text.substring(0, 3);
//     戻り値      メソッド名  第1引数 第2引数
```

Javadocを読むときは、次の順番でメモします。

1. 何を呼び出すメソッドか
2. 引数へ何を渡すか
3. 戻り値を何型で受けるか
4. どの条件で例外が発生するか
5. 最小コードで読み方が正しいか確認する

---

## 4. ハンズオン

目的:
- Javadoc参照 -> 実装 -> 実行確認 の流れを体で覚える

完了条件:
- 初級・中級・応用の3本で、コードを実行して仕様どおりの挙動を確認できる
- 提出は不要（学習者の自己確認のみ）

作成ファイル:
- `~/order-management-springboot/practice/java/handson20/DocDrivenApiDemo.java`
- `~/order-management-springboot/practice/java/handson20/HttpServerDocDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson20
cd ~/order-management-springboot/practice/java/handson20
```

### Step 1: 初級（String / List）
まず Javadoc を読む:
- `String.substring(int beginIndex, int endIndex)`
  https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/lang/String.html
- `List.of(E... elements)`
  https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/util/List.html

確認ポイント:
1. `substring(beginIndex, endIndex)` は終端を含むか
2. `substring` が例外になる条件は何か
3. `List.of` の戻り値は変更可能か
4. `List.of` は `null` 要素を許可するか

`DocDrivenApiDemo.java` を次の内容で作成:

```java
import java.util.List; // List 利用のための import

public class DocDrivenApiDemo { // Javadocを元に挙動確認するクラス
    public static void main(String[] args) {
        String orderId = "ORD-2026-0001"; // 元文字列
        String prefix = orderId.substring(0, 3); // beginIndex を含み、endIndex は含まない
        System.out.println("prefix=" + prefix); // ORD が出ることを確認

        List<String> statuses = List.of("PAID", "PENDING", "PAID"); // 変更不可リストを作成
        System.out.println("size=" + statuses.size()); // 3 を確認

        try {
            statuses.add("CANCELLED"); // 変更不可のため例外確認
        } catch (UnsupportedOperationException ex) {
            System.out.println("List.of は変更不可");
        }

        try {
            List.of("OK", null); // null 非許可の確認
        } catch (NullPointerException ex) {
            System.out.println("List.of は null 要素を許可しない");
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 DocDrivenApiDemo.java
java DocDrivenApiDemo
```

期待出力例:
```text
prefix=ORD
List.of は変更不可
List.of は null 要素を許可しない
```

### Step 2: 中級（Stream）
まず Javadoc を読む:
- `Stream.filter(Predicate)`
- `Stream.toList()`
  https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/util/stream/Stream.html

確認ポイント:
1. `filter` は中間操作か終端操作か
2. `toList()` の戻り値は変更可能か
3. `toList()` の実装型は保証されるか

`DocDrivenApiDemo.java` を次の内容に更新:

```java
import java.util.List; // List 利用のための import
import java.util.stream.Stream; // Stream 利用のための import

public class DocDrivenApiDemo { // Stream API の挙動確認クラス
    public static void main(String[] args) {
        List<Integer> amounts = List.of(1200, 3000, 800, 4500); // 元データ

        final int[] evaluated = {0}; // filter 評価回数の確認用
        Stream<Integer> pipeline = amounts.stream()
                .filter(amount -> { // 中間操作（この時点では評価されない）
                    evaluated[0]++;
                    return amount >= 1000;
                });

        System.out.println("terminal前の評価回数=" + evaluated[0]); // 0 を期待

        List<Integer> filtered = pipeline.toList(); // 終端操作で初めて評価される
        System.out.println("terminal後の評価回数=" + evaluated[0]); // 4 を期待
        System.out.println("filtered=" + filtered); // [1200, 3000, 4500] を期待

        try {
            filtered.add(9999); // toList の戻り値は変更不可
        } catch (UnsupportedOperationException ex) {
            System.out.println("Stream.toList の戻り値は変更不可");
        }
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 DocDrivenApiDemo.java
java DocDrivenApiDemo
```

期待出力例:
```text
terminal前の評価回数=0
terminal後の評価回数=4
filtered=[1200, 3000, 4500]
Stream.toList の戻り値は変更不可
```

### Step 3: 応用（HttpServer）
まず Javadoc を読む:
- `HttpServer.create(InetSocketAddress addr, int backlog)`
- `HttpServer.createContext(String path, HttpHandler handler)`
- `HttpServer.setExecutor(Executor executor)`
- `HttpServer.start()`
  https://docs.oracle.com/javase/jp/17/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html
- `HttpExchange.getRequestMethod()`
- `HttpExchange.getResponseHeaders()`
- `HttpExchange.sendResponseHeaders(int rCode, long responseLength)`
- `HttpExchange.getResponseBody()`
- `HttpExchange.close()`
  https://docs.oracle.com/javase/jp/17/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpExchange.html

確認ポイント:
1. `backlog` の意味
2. `backlog` が `0` 以下のときの扱い
3. `setExecutor(null)` の意味
4. `start()` を呼んだ後に何が起きるか
5. `getRequestMethod()`が何を返すか
6. `sendResponseHeaders(...)`の2つの引数が何を表すか
7. レスポンス本文をどこへ書き込むか

`HttpServer`と`HttpExchange`は役割が異なります。

| 型 | このコードでの役割 |
| --- | --- |
| `HttpServer` | ポートを開き、URLごとの担当処理を登録して受付を開始する |
| `HttpExchange` | 1回分のリクエスト情報を受け取り、その通信へレスポンスを返す |

1回の通信は次の順番で処理します。

```text
getRequestMethod()
  リクエストがGETか確認する
        ↓
getResponseHeaders().set(...)
  返す本文の種類を設定する
        ↓
sendResponseHeaders(...)
  HTTPステータスと本文サイズを送る
        ↓
getResponseBody().write(...)
  本文を書き込む
        ↓
close()
  通信を終了する
```

`HttpServerDocDemo.java` を次の内容で作成:

```java
import com.sun.net.httpserver.HttpExchange; // リクエスト/レスポンスの入れ物
import com.sun.net.httpserver.HttpServer; // JDK標準HTTPサーバー

import java.io.IOException; // 入出力例外
import java.net.InetSocketAddress; // ポート指定
import java.nio.charset.StandardCharsets; // UTF-8 文字コード

public class HttpServerDocDemo { // HttpServer の最小確認クラス
    public static void main(String[] args) throws IOException {
        int port = 8090; // 待受ポート
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0); // backlog=0 はシステム既定
        server.createContext("/health", HttpServerDocDemo::handleHealth); // /health の担当を登録
        server.setExecutor(null); // 既定の実行方式
        server.start(); // 受付開始

        System.out.println("started: http://localhost:" + port + "/health"); // 起動確認表示
    } // main メソッドの終わり

    private static void handleHealth(HttpExchange exchange) throws IOException {
        // getRequestMethod()は、受け取ったHTTPメソッドをGETなどの文字列で返す
        String requestMethod = exchange.getRequestMethod();

        // equalsIgnoreCase(...)は大文字・小文字を区別せず比較する
        if (!"GET".equalsIgnoreCase(requestMethod)) {
            // 405は許可されていないHTTPメソッド。本文を返さないため長さは-1
            exchange.sendResponseHeaders(405, -1);
            exchange.close(); // このHTTP通信を終了する
            return;
        }

        // HTTPでは本文をバイト列で送るため、文字列OKをUTF-8のbyte[]へ変換する
        byte[] body = "OK".getBytes(StandardCharsets.UTF_8);

        // getResponseHeaders()でレスポンスヘッダーを取得し、本文の種類を設定する
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

        // sendResponseHeaders(ステータス, 本文のバイト数)で200 OKと本文サイズを送る
        exchange.sendResponseHeaders(200, body.length);

        // getResponseBody()で本文の書き込み先を取得し、用意したバイト列を書き込む
        exchange.getResponseBody().write(body);

        exchange.close(); // レスポンスを書き終えたため通信を終了する
    } // handleHealth の終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 HttpServerDocDemo.java
java HttpServerDocDemo
```

別ターミナルで確認:
```bash
curl -i http://localhost:8090/health
```

期待レスポンス例:
```text
HTTP/1.1 200 OK

OK
```

終了方法:
- サーバー起動中のターミナルで `Ctrl + C`

---

## 5. 解答例

### Step 1 解答例（初級）
1. `substring(beginIndex, endIndex)` は `beginIndex` を含み、`endIndex` は含まない
2. `beginIndex < 0`、`endIndex > 文字列長`、`beginIndex > endIndex` の場合は `IndexOutOfBoundsException`
3. `List.of` の戻り値は変更不可
4. `List.of` は `null` 要素を許可しない

実行で確認できること:
- `prefix=ORD` が出力される
- `UnsupportedOperationException` / `NullPointerException` を捕捉できる

根拠ページ:
- `String`: https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/lang/String.html
- `List`: https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/util/List.html

### Step 2 解答例（中級）
1. `filter` は中間操作
2. `toList` は終端操作で、戻り値は変更不可
3. `toList` の戻り値の具体実装型は保証されない

実行で確認できること:
- 終端操作前は `filter` が評価されない
- `toList` 結果への `add` は失敗する

根拠ページ:
- `Stream`: https://docs.oracle.com/javase/jp/17/docs/api/java.base/java/util/stream/Stream.html

### Step 3 解答例（応用）
1. `backlog` は受理待ち接続キューに関する値
2. `backlog` が `0` 以下ならシステム既定値が使われる
3. `setExecutor(null)` は既定の実行方式を使う
4. `start()` 後にリクエスト受付が始まる

実行で確認できること:
- `curl` で `200 OK` と本文 `OK` が返る
- `start()` 前はアクセスできず、`start()` 後にアクセスできる

根拠ページ:
- `HttpServer`: https://docs.oracle.com/javase/jp/17/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html

---

## 6. つまずきポイント
- 仕様確認なしでコードを書き始める
  -> 先に対象メソッドの `Method Detail` を読む
- 例外条件を読まずに境界値テストを省く
  -> `Throws` と入力制約を先にチェックする
- 「動いた」だけで終える
  -> 出力と例外が仕様どおりかまで確認する
