## 2. Java 基礎（午後）

### この資料の使い方（最初に読んでください）
対象:
- Javaが初めての人
- 「まず何を実行すればよいか」を迷わず進めたい人

進め方（毎Step共通）:
1. コードをそのまま書く（写経）
2. `javac` でコンパイルする
3. `java` で実行する
4. 「期待出力」と一致するか確認する
5. ずれたら、まず `;` / `{}` / クラス名とファイル名一致を確認する

この資料のゴール:
- 1人で `.java` ファイルを作成し、コンパイルと実行を繰り返せる
- `if/else`、`for`、クラス/メソッド、List/Map を最低限使える
- Day1 の Spring Boot 演習に入る前の基礎操作で迷わない

### 2-0. 最初の5分チェック（必須）
最初にここだけ実行し、環境と作業場所を固定します。

1. 演習フォルダへ移動
```bash
cd practice/day0/java
pwd
ls
```

2. Java コマンド確認
```bash
java -version
javac -version
```

3. 期待状態
- `pwd` で `.../order-management-springboot/practice/day0/java` が表示される
- `java -version` と `javac -version` がどちらも表示される
- `javac` が not found にならない

失敗したとき:
- `java` / `javac` が見つからない場合は、後半の「つまずきポイント」「エラー早見表」を先に確認
- フォルダが違う場合は `cd` をやり直してから続行

#### Javaとは（HTML/CSSとの違い）
- HTML/CSSは「画面をどう見せるか」を書く言語
- Javaは「処理をどう動かすか」を書くプログラミング言語
- この研修では、Javaで業務ロジック（出勤/退勤ルール）を実装する

#### JDK / JRE / JVM の違い（初心者向け）
| 用語 | 役割 | 含まれるもの | この研修での見え方 |
|---|---|---|---|
| JVM | Javaプログラムを実行する本体（仮想マシン） | `.class` を実行する実行エンジン | `java Hello` 実行時に裏で動く |
| JRE | Javaを「実行するため」の一式 | JVM + 標準ライブラリ | 実行だけならこれで可能（開発には不足） |
| JDK | Javaを「開発するため」の一式 | JRE + `javac` などの開発ツール | この研修で必須。`javac` と `java` を使う |

補足:
- `javac` が使えるのは JDK を入れているから
- 研修では「JDKを導入すれば、実行（JRE/JVM）もコンパイルもできる」と覚えればOK

#### Javaの基本構文
```java
public class Sample {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

この例の意味:
- `public class Sample` はクラス定義
- `main(...)` は実行開始地点
- `System.out.println(...)` は画面出力
- `{}` は処理の範囲、`;` は文の終わり

#### Javaの実行の流れ（今回の演習）
1. `javac` で `.java` をコンパイルして `.class` を作る
2. `java` で `.class` を実行する
3. エラーが出たら「行番号」「メッセージ」を見て修正する

#### この演習で見るJavaの考え方
- クラス: データと処理をまとめる単位
- メソッド: クラスの中の処理単位
- 変数: 値を一時的に保持する入れ物
- 条件分岐/ループ: ルールに応じた処理制御

#### Day0とDay1の違い（`package` / `import`）
- Day0は文法理解を優先するため、`package` なしで最小実行している
- Day1以降は実アプリ構成に合わせて `package com.shinesoft...;` を付ける
- `import` は別パッケージのクラスを使う宣言（`List` / `Map` で既に体験済み）

例（Day1以降の形）:
```java
package com.shinesoft.attendance.web;

import java.time.LocalDate;
```

### 2-1. 最初のJava（Hello）

目的:
- Javaの最小実行単位（クラス + `main`）を理解する
- `javac` → `java` の基本サイクルを体験する

完了条件:
- `Hello.java` がコンパイル成功する
- `java Hello` 実行で `Hello Java` が1行表示される
作成ファイル: `practice/day0/java/Hello.java`  
HTML/CSSと同様に「Stepごとにコードを更新して実行確認」します。

#### Step 0: 空のクラスを作る
`Hello.java` を次の内容で作成:

```java
public class Hello {
}
```

確認:
- コンパイルが通るか
  ```bash
  cd practice/day0/java
  javac -encoding UTF-8 Hello.java
  ```

コード解説:
- `public class Hello` は「公開クラス Hello を定義する」
- クラス名 `Hello` とファイル名 `Hello.java` は一致必須
- この段階は実行入口 `main` がないため、`java Hello` はまだ不可

#### Step 1: `main` メソッドを追加
`Hello.java` を次の内容に更新:

```java
public class Hello {
    public static void main(String[] args) {
    }
}
```

確認:
- コンパイルが通るか
  ```bash
  javac -encoding UTF-8 Hello.java
  ```
- 実行しても何も表示されないか
  ```bash
  java Hello
  ```

コード解説:
- `public static void main(String[] args)` は Java の実行開始地点
- `public`: JVMから呼び出せるよう公開
- `static`: インスタンス生成なしで呼び出し可能
- `void`: 戻り値なし
- `String[] args`: 実行時引数を受け取る配列
- `{ }` の中身が空なので、実行しても出力はない

#### Step 2: 画面出力を追加（完成）
`Hello.java` を次の内容に更新:

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello Java");
    }
}
```

確認:
- コンパイルと実行
  ```bash
  javac -encoding UTF-8 Hello.java
  java Hello
  ```
- `Hello Java` が表示される

コード解説:
- `System.out.println(...)` は標準出力への1行表示
- `"Hello Java"` は文字列リテラル（ダブルクォートで囲む）
- 末尾の `;` は文の終了記号

### 2-2. 変数と計算

目的:
- 変数に値を入れて計算し、結果を表示する流れを理解する

完了条件:
- `Calc.java` が実行でき、`合計: 3600`（3 * 1200）と表示される
作成ファイル: `practice/day0/java/Calc.java`

#### Step 0: 骨組みを作る
`Calc.java` を次の内容で作成:

```java
public class Calc {
    public static void main(String[] args) {
    }
}
```

#### Step 1: 変数を追加
`Calc.java` を次の内容に更新:

```java
public class Calc {
    public static void main(String[] args) {
        int quantity = 3;
        int price = 1200;
    }
}
```

コード解説:
- `int` は整数型
- `quantity` / `price` は変数名
- `=` は代入演算子（右辺の値を左辺に入れる）

#### Step 2: 計算と出力を追加（完成）
`Calc.java` を次の内容に更新:

```java
public class Calc {
    public static void main(String[] args) {
        int quantity = 3;
        int price = 1200;
        int total = quantity * price;
        System.out.println("合計: " + total);
    }
}
```

コード解説:
- `quantity * price` で合計計算
- `total` に結果を保持してから出力
- `"合計: " + total` は文字列連結

実行:
```bash
cd practice/day0/java
javac -encoding UTF-8 Calc.java
java Calc
```

ミニ演習:
- `quantity` を `5` に変更
- `price` を `980` に変更

### 2-3. 条件分岐（if/else）

目的:
- 条件に応じて処理を分ける（if/else）基本を理解する

完了条件:
- `workedHours = 8` で `本日は勤務完了` が表示される
- `workedHours = 6` に変えると `勤務中です` が表示される
作成ファイル: `practice/day0/java/StatusCheck.java`

#### Step 0: 変数だけ作る
`StatusCheck.java` を次の内容で作成:

```java
public class StatusCheck {
    public static void main(String[] args) {
        int workedHours = 8;
        System.out.println(workedHours);
    }
}
```

#### Step 1: `if` だけ追加
`StatusCheck.java` を次の内容に更新:

```java
public class StatusCheck {
    public static void main(String[] args) {
        int workedHours = 8;

        if (workedHours >= 8) {
            System.out.println("本日は勤務完了");
        }
    }
}
```

コード解説:
- `if (条件)` の条件が `true` のときだけ中を実行
- `>=` は「以上」の比較

#### Step 2: `else` を追加（完成）
`StatusCheck.java` を次の内容に更新:

```java
public class StatusCheck {
    public static void main(String[] args) {
        int workedHours = 8;

        if (workedHours >= 8) {
            System.out.println("本日は勤務完了");
        } else {
            System.out.println("勤務中です");
        }
    }
}
```

コード解説:
- `else` は `if` が `false` の場合に実行
- 条件分岐で業務ルールの分かれ道を表現できる

実行:
```bash
javac -encoding UTF-8 StatusCheck.java
java StatusCheck
```

ミニ演習:
- `workedHours = 6` に変更して出力を確認

### 2-4. ループ

目的:
- 同じ処理を複数回実行する `for` ループの基本形を理解する

完了条件:
- `勤務日: 1日目` 〜 `勤務日: 3日目` が3行表示される
作成ファイル: `practice/day0/java/LoopDemo.java`

#### Step 0: 1回だけ表示
```java
public class LoopDemo {
    public static void main(String[] args) {
        System.out.println("勤務日: 1日目");
    }
}
```

#### Step 1: `for` ループを追加（完成）
`LoopDemo.java` を次の内容に更新:

```java
public class LoopDemo {
    public static void main(String[] args) {
        for (int i = 1; i <= 3; i++) {
            System.out.println("勤務日: " + i + "日目");
        }
    }
}
```

コード解説:
- `for (初期化; 条件; 更新)` の順で評価
- `int i = 1` で開始
- `i <= 3` の間繰り返す
- `i++` で1増加

実行:
```bash
javac -encoding UTF-8 LoopDemo.java
java LoopDemo
```

ミニ演習:
- `i <= 5` に変更して表示回数を増やす

### 2-5. クラスとメソッド

目的:
- クラスにデータ（フィールド）と処理（メソッド）をまとめる考え方を理解する

完了条件:
- `Order.java` / `OrderDemo.java` を実行し、`Laptop 合計: 240000` が表示される
作成ファイル: `practice/day0/java/Order.java`

#### Step 0: フィールドだけ作る
`Order.java` を次の内容で作成:

```java
public class Order {
    String productName;
    int quantity;
    int price;
}
```

コード解説:
- クラスは「データ（フィールド）」と「処理（メソッド）」をまとめる単位
- ここでは商品名・数量・単価を保持している

#### Step 1: メソッドを追加
`Order.java` を次の内容に更新:

```java
public class Order {
    String productName;
    int quantity;
    int price;

    int calcTotal() {
        return quantity * price;
    }
}
```

コード解説:
- `int calcTotal()` は `int` を返すメソッド
- `return` は呼び出し元へ値を返す命令

作成ファイル: `practice/day0/java/OrderDemo.java`

#### Step 2: 利用側クラスを作る（完成）
`OrderDemo.java` を次の内容で作成:

```java
public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order();
        order.productName = "Laptop";
        order.quantity = 2;
        order.price = 120000;

        int total = order.calcTotal();
        System.out.println(order.productName + " 合計: " + total);
    }
}
```

コード解説:
- `new Order()` でインスタンスを生成
- `order.xxx` でフィールドやメソッドにアクセス
- `order.calcTotal()` でクラス内ロジックを再利用

実行:
```bash
javac -encoding UTF-8 Order.java OrderDemo.java
java OrderDemo
```

ミニ演習:
- `quantity` を `3` に変更
- `productName` を `Mouse` に変更

### 2-6. メソッドの引数と戻り値

目的:
- 引数と戻り値を使って、再利用できるメソッドを作る

完了条件:
- `MathUtilDemo` 実行で `合計: 3000` が表示される
- 引数を変えると結果が変わることを確認できる
作成ファイル: `practice/day0/java/MathUtil.java`

#### Step 0: 引数なしメソッド
`MathUtil.java` を次の内容で作成:

```java
public class MathUtil {
    static int calcTotal() {
        return 1000;
    }
}
```

#### Step 1: 引数ありに更新（完成）
`MathUtil.java` を次の内容に更新:

```java
public class MathUtil {
    static int calcTotal(int quantity, int price) {
        return quantity * price;
    }
}
```

コード解説:
- 引数を使うと「外から渡された値」で計算できる
- `static` はクラス名から直接呼び出せる

作成ファイル: `practice/day0/java/MathUtilDemo.java`

#### Step 2: 呼び出し側を作る
`MathUtilDemo.java` を次の内容で作成:

```java
public class MathUtilDemo {
    public static void main(String[] args) {
        int total = MathUtil.calcTotal(2, 1500);
        System.out.println("合計: " + total);
    }
}
```

コード解説:
- `MathUtil.calcTotal(2, 1500)` で引数を渡して呼び出す
- 引数順序（数量、単価）を逆にすると意味が変わる

実行:
```bash
javac -encoding UTF-8 MathUtil.java MathUtilDemo.java
java MathUtilDemo
```

ミニ演習:
- `calcTotal(3, 980)` に変更

### 2-7. List / Map（コレクション）

目的:
- 複数データを扱う `List` と、キーで値を管理する `Map` の違いを理解する

完了条件:
- 商品名が複数行表示される
- `Mouse数量: 5` が表示される
作成ファイル: `practice/day0/java/ListMapDemo.java`

#### Step 0: Listだけ作る
`ListMapDemo.java` を次の内容で作成:

```java
import java.util.ArrayList;
import java.util.List;

public class ListMapDemo {
    public static void main(String[] args) {
        List<String> products = new ArrayList<>();
        products.add("Laptop");
        products.add("Mouse");
        products.add("Keyboard");

        System.out.println(products);
    }
}
```

#### Step 1: for-eachで出力
`ListMapDemo.java` を次の内容に更新:

```java
import java.util.ArrayList;
import java.util.List;

public class ListMapDemo {
    public static void main(String[] args) {
        List<String> products = new ArrayList<>();
        products.add("Laptop");
        products.add("Mouse");
        products.add("Keyboard");

        for (String p : products) {
            System.out.println("商品: " + p);
        }
    }
}
```

#### Step 2: Mapを追加（完成）
`ListMapDemo.java` を次の内容に更新:

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMapDemo {
    public static void main(String[] args) {
        List<String> products = new ArrayList<>();
        products.add("Laptop");
        products.add("Mouse");
        products.add("Keyboard");

        for (String p : products) {
            System.out.println("商品: " + p);
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("Laptop", 2);
        counts.put("Mouse", 5);

        System.out.println("Mouse数量: " + counts.get("Mouse"));
    }
}
```

コード解説:
- `List<String>` は順序を持つ複数データ
- `Map<String, Integer>` はキーと値の組
- `put` で登録、`get` で取り出し

実行:
```bash
javac -encoding UTF-8 ListMapDemo.java
java ListMapDemo
```

### 2-8. ミニ制作（30〜45分）

目的:
- ここまでの要素（クラス、メソッド、条件分岐）をまとめて使う

完了条件:
- 未出勤→出勤→二重出勤→退勤の順で、状態遷移の動作を確認できる
- 実行結果から「どの条件分岐に入ったか」を説明できる

進め方（小分け推奨）:
1. `status` 表示だけ先に確認する
2. `clockIn` だけ実装して二重出勤メッセージを確認する
3. `clockOut` を追加して遷移完了させる
「勤怠の状態遷移」をJavaだけで再現します。

作成ファイル: `practice/day0/java/Attendance.java`

#### Step 0: 状態フィールドだけ作る
`Attendance.java` を次の内容で作成:

```java
public class Attendance {
    String status = "NOT_STARTED"; // NOT_STARTED / WORKING / FINISHED
}
```

#### Step 1: 状態遷移メソッドを追加（完成）
`Attendance.java` を次の内容に更新:

```java
public class Attendance {
    String status = "NOT_STARTED"; // NOT_STARTED / WORKING / FINISHED

    void clockIn() {
        if ("NOT_STARTED".equals(status)) {
            status = "WORKING";
        } else {
            System.out.println("すでに出勤済みです");
        }
    }

    void clockOut() {
        if ("WORKING".equals(status)) {
            status = "FINISHED";
        } else if ("NOT_STARTED".equals(status)) {
            System.out.println("先に出勤してください");
        } else {
            System.out.println("すでに退勤済みです");
        }
    }
}
```

コード解説:
- `status` で現在状態を保持
- `clockIn` と `clockOut` にルールを集約
- `"文字列".equals(status)` は null安全な比較

作成ファイル: `practice/day0/java/AttendanceDemo.java`

#### Step 2: 検証用mainを作る
`AttendanceDemo.java` を次の内容で作成:

```java
public class AttendanceDemo {
    public static void main(String[] args) {
        Attendance a = new Attendance();
        System.out.println("状態: " + a.status);

        a.clockOut();   // 未出勤で退勤
        a.clockIn();    // 出勤
        a.clockIn();    // 二重出勤
        a.clockOut();   // 退勤

        System.out.println("状態: " + a.status);
    }
}
```

コード解説:
- 1つのインスタンスに対して順番に操作し、状態変化を確認
- コメントは「何をテストしているか」の目印

実行:
```bash
cd practice/day0/java
javac -encoding UTF-8 *.java
java AttendanceDemo
```

コマンド解説:
- `javac -encoding UTF-8 *.java`: カレントディレクトリの `.java` をまとめてコンパイル
- `java AttendanceDemo`: `public static void main` を持つクラスを実行

### 2-9. 手動ライブラリ追加を体験（15〜20分）
目的:
- Mavenを使わずに「ライブラリを使える状態にする」手順を体験する
- Day1の `pom.xml` が何を簡略化しているかを理解する

完了条件:
- `lib/manual-banner-1.0.jar` を自分で作成できる
- クラスパス指定なしだと失敗し、指定ありで `ManualLibDemo` が実行できる

#### Step 0: ミニライブラリを自作する
最初にディレクトリを作成:
```bash
cd practice/day0/java
mkdir -p libsrc/com/shinesoft/util
mkdir -p lib/classes
```

作成ファイル: `practice/day0/java/libsrc/com/shinesoft/util/BannerUtil.java`

```java
package com.shinesoft.util;

public class BannerUtil {
    public static String banner(String text) {
        return "[[ " + text + " ]]";
    }
}
```

コンパイルして jar 化:
```bash
javac -encoding UTF-8 -d lib/classes libsrc/com/shinesoft/util/BannerUtil.java
jar --create --file lib/manual-banner-1.0.jar -C lib/classes .
```

確認:
```bash
jar --list --file lib/manual-banner-1.0.jar
```

#### Step 1: ライブラリ利用側を作る
作成ファイル: `practice/day0/java/ManualLibDemo.java`

```java
import com.shinesoft.util.BannerUtil;

public class ManualLibDemo {
    public static void main(String[] args) {
        System.out.println(BannerUtil.banner("Manual dependency"));
    }
}
```

#### Step 2: 失敗パターンを先に確認（クラスパス未指定）
```bash
javac -encoding UTF-8 ManualLibDemo.java
```

期待結果:
- `package com.shinesoft.util does not exist` などのエラーが出る

#### Step 3: クラスパスを指定して成功させる
```bash
javac -encoding UTF-8 -cp lib/manual-banner-1.0.jar ManualLibDemo.java
java -cp ".;lib/manual-banner-1.0.jar" ManualLibDemo
```

期待出力:
```text
[[ Manual dependency ]]
```

学習ポイント:
- jar を作る、配置する、`-cp` を指定する、実行時にも `-cp` を指定する、を毎回管理する必要がある
- Day1ではこの管理を `pom.xml` と `mvn` が肩代わりする

### 2-10. 手動Web起動を体験（20〜25分）
目的:
- Spring BootなしでWebサーバーを立てると、どこまで手作業が必要か体験する
- Day1の `@Controller` / `@GetMapping` が何を簡略化するか理解する

完了条件:
- `http://localhost:8080/` にアクセスしてHTMLが表示される
- `Ctrl + C` で停止できる

作成ファイル: `practice/day0/java/MiniWebServer.java`

```java
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
```

実行:
```bash
cd practice/day0/java
javac -encoding UTF-8 --add-modules jdk.httpserver MiniWebServer.java
java --add-modules jdk.httpserver MiniWebServer
```

確認:
- ブラウザで `http://localhost:8080/` を開く
- 画面に `Day0 Manual Web` が表示される
- 停止は `Ctrl + C`

学習ポイント:
- ルーティング、レスポンスヘッダ、ステータスコード、ボディ返却を自分で書く必要がある
- Day1では `@GetMapping("/")` と `return "index"` で同じ意図をより短く表現できる

### 2-11. Day1で出るJava構文を先取り（45〜60分）
目的:
- Day1 `maven-sandbox` で急に出るJava記法を、Spring本体に入る前に体験する
- 「何をしているコードか」を読める状態でDay1に進む

完了条件:
- `private final` とコンストラクタ注入の形を説明できる
- `||`, `isBlank()`, `trim()` の意味を説明できる
- `@Controller` / `@GetMapping` / `@RequestParam` / `@Test` の見た目と役割を説明できる
- `import static` と `assertEquals` の使い方を説明できる

#### Step 0: 作業フォルダを作る
このStepで新しく覚えること:
- Day1向けの予習コードを通常演習と分離する理由

```bash
cd practice/day0/java
mkdir -p day1-bridge/teststyle
```

意味:
- `day1-bridge` を作ることで、Day0の基本演習ファイルと混ざらず復習しやすくなる
- `teststyle` は Step 4 の `package teststyle;` とフォルダを一致させるため

#### Step 1: コンストラクタ + `private final`（DIの形）
このStepで新しく覚えること:
- `private final`（再代入しないフィールド）
- コンストラクタで依存を受け取る形（コンストラクタ注入の土台）

作成ファイル: `practice/day0/java/day1-bridge/ConstructorDiDemo.java`

```java
public class ConstructorDiDemo {
    static class MessageService {
        String createMessage(String name) {
            if (name == null || name.isBlank()) {
                return "Hello, guest";
            }
            return "Hello, " + name.trim();
        }
    }

    static class GreetingControllerLike {
        private final MessageService messageService; // 再代入しない依存

        GreetingControllerLike(MessageService messageService) { // コンストラクタで受け取る
            this.messageService = messageService;
        }

        String hello(String name) {
            return messageService.createMessage(name);
        }
    }

    public static void main(String[] args) {
        MessageService service = new MessageService();
        GreetingControllerLike controller = new GreetingControllerLike(service);
        System.out.println(controller.hello("  Shinesoft  "));
    }
}
```

実行:
```bash
cd practice/day0/java/day1-bridge
javac -encoding UTF-8 ConstructorDiDemo.java
java ConstructorDiDemo
```

期待出力:
```text
Hello, Shinesoft
```

行ごとの意味（重要行）:
- `private final MessageService messageService;`
  - `private`: このクラス内からのみアクセス
  - `final`: 1回代入したら参照先を変更しない
- `GreetingControllerLike(MessageService messageService)`
  - コンストラクタ。外から依存オブジェクトを受け取る入口
- `this.messageService = messageService;`
  - 受け取った依存をフィールドへ保存

なぜこの出力になるか:
- `"  Shinesoft  "` が `trim()` で前後空白除去され、`Hello, Shinesoft` になる

1分ミニ改造:
- `controller.hello("   ")` に変更して `Hello, guest` になることを確認

よくあるミス:
- `final` フィールドに後から再代入しようとしてエラーになる
- コンストラクタの引数名と `this.` の意味を混同する

Day1でどこに出るか:
- `maven-sandbox/README.md` の `GreetingController` で `private final` とコンストラクタ注入が出る

#### Step 2: `||`, `isBlank()`, `trim()` の挙動
このStepで新しく覚えること:
- `||`（OR条件）の評価
- 文字列の空判定と整形

作成ファイル: `practice/day0/java/day1-bridge/StringRuleDemo.java`

```java
public class StringRuleDemo {
    static String normalize(String name) {
        if (name == null || name.isBlank()) {
            return "guest";
        }
        return name.trim();
    }

    public static void main(String[] args) {
        System.out.println(normalize(null));
        System.out.println(normalize("   "));
        System.out.println(normalize("  Alice  "));
    }
}
```

実行:
```bash
javac -encoding UTF-8 StringRuleDemo.java
java StringRuleDemo
```

期待出力:
```text
guest
guest
Alice
```

行ごとの意味（重要行）:
- `if (name == null || name.isBlank())`
  - 左が `true` なら右を見なくても `true`（短絡評価）
- `name.trim()`
  - 前後の空白だけを削る（途中の空白は残る）

なぜこの出力になるか:
- `null` と `"   "` は条件式で `guest`
- `"  Alice  "` は `trim()` で `"Alice"` になる

1分ミニ改造:
- `normalize(" A B ")` を追加して、`A B`（途中空白は残る）を確認

よくあるミス:
- `isBlank()` と `isEmpty()` の違いを混同する
- `null` チェックより先に `isBlank()` を呼んで `NullPointerException` になる

Day1でどこに出るか:
- `GreetingService` / `GreetingCalculator` の `name == null || name.isBlank()` と `trim()`

#### Step 3: アノテーション記法 + `@RequestParam` 風の属性
このStepで新しく覚えること:
- `@...` の基本形（アノテーション宣言と付与）
- `name = "..."`, `required = false` のような属性指定の読み方

作成ファイル: `practice/day0/java/day1-bridge/AnnotationAndRequestParamDemo.java`

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ControllerLike {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface GetMappingLike {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface RequestParamLike {
    String name();

    boolean required() default true;
}

@ControllerLike
class GreetingControllerLike {
    @GetMappingLike("/hello")
    public String hello(@RequestParamLike(name = "name", required = false) String name) {
        return name;
    }
}

public class AnnotationAndRequestParamDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = GreetingControllerLike.class;
        Method method = clazz.getDeclaredMethod("hello", String.class);
        Parameter param = method.getParameters()[0];

        GetMappingLike mapping = method.getAnnotation(GetMappingLike.class);
        RequestParamLike requestParam = param.getAnnotation(RequestParamLike.class);

        System.out.println("ControllerLike: " + clazz.isAnnotationPresent(ControllerLike.class));
        System.out.println("GetMappingLike.value: " + mapping.value());
        System.out.println("RequestParamLike.name: " + requestParam.name());
        System.out.println("RequestParamLike.required: " + requestParam.required());
    }
}
```

実行:
```bash
javac -encoding UTF-8 AnnotationAndRequestParamDemo.java
java AnnotationAndRequestParamDemo
```

期待出力:
```text
ControllerLike: true
GetMappingLike.value: /hello
RequestParamLike.name: name
RequestParamLike.required: false
```

行ごとの意味（重要行）:
- `@interface ControllerLike {}`:
  - 「アノテーション型」を定義している
- `@GetMappingLike("/hello")`:
  - `value` 属性への省略記法（`value = "/hello"` と同義）
- `@RequestParamLike(name = "name", required = false)`:
  - 属性を明示した記法。Day1の `@RequestParam` と同じ読み方
- `method.getAnnotation(...)` / `param.getAnnotation(...)`:
  - 付与したアノテーション情報を読み取って出力している

なぜこの出力になるか:
- クラス/メソッド/引数に付けた値を、実行時に読み取って表示しているため

1分ミニ改造:
- `@GetMappingLike("/hi")` に変えて `GetMappingLike.value` の出力差分を確認

よくあるミス:
- `@interface` を通常クラスと勘違いする
- `@Retention(RetentionPolicy.RUNTIME)` を外して実行時に取得できなくなる

Day1でどこに出るか:
- `@Controller`, `@GetMapping`, `@RequestParam(name=..., required=false)` の見方そのもの

#### Step 4: `@Test` 風 + `import static` + `assertEquals`
このStepで新しく覚えること:
- `import static` でメソッド名だけで呼び出せること
- `assertEquals` で期待値と実際値を比較する考え方
- `@Test` 相当の「テストメソッド目印」記法

作成ファイル: `practice/day0/java/day1-bridge/teststyle/AssertLite.java`

```java
package teststyle;

public class AssertLite {
    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new IllegalStateException("expected=" + expected + ", actual=" + actual);
        }
    }
}
```

作成ファイル: `practice/day0/java/day1-bridge/teststyle/TestStyleDemo.java`

```java
package teststyle;

import static teststyle.AssertLite.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface TestCase {
}

public class TestStyleDemo {
    @TestCase
    static void add_returnsSum() {
        assertEquals(5, 2 + 3);
    }

    @TestCase
    static void blank_returnsGuest() {
        String value = " ".isBlank() ? "guest" : "x";
        assertEquals("guest", value);
    }

    public static void main(String[] args) throws Exception {
        int passed = 0;
        for (Method m : TestStyleDemo.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(TestCase.class)) {
                m.invoke(null);
                passed++;
                System.out.println("[PASS] " + m.getName());
            }
        }
        System.out.println("PASSED: " + passed);
    }
}
```

実行:
```bash
javac -encoding UTF-8 teststyle/AssertLite.java teststyle/TestStyleDemo.java
java -cp . teststyle.TestStyleDemo
```

期待出力:
```text
[PASS] add_returnsSum
[PASS] blank_returnsGuest
PASSED: 2
```

行ごとの意味（重要行）:
- `import static teststyle.AssertLite.assertEquals;`
  - `AssertLite.assertEquals(...)` を `assertEquals(...)` と短く書ける
- `@TestCase`
  - そのメソッドをテスト対象として扱う目印
- `if (m.isAnnotationPresent(TestCase.class))`
  - `@TestCase` が付いたメソッドだけ実行する

なぜこの出力になるか:
- 2つのテストメソッドが例外なく通過したので `PASS` が2件表示される

1分ミニ改造:
- `assertEquals(6, 2 + 3);` に変えて失敗メッセージを確認して戻す

よくあるミス:
- `package teststyle;` とフォルダ名 `teststyle/` を一致させない
- `import static` の対象を通常 `import` と同じ感覚で書いてエラーになる

Day1でどこに出るか:
- JUnit の `@Test` と `Assertions.assertEquals(...)` の読み方

対応関係（Day1へ）
- `ControllerLike` / `GetMappingLike` / `RequestParamLike` -> `@Controller` / `@GetMapping` / `@RequestParam`
- `TestCase` / `AssertLite.assertEquals` -> `@Test` / `Assertions.assertEquals`
- `private final` + コンストラクタ受け取り -> コンストラクタ注入（DI）の基本形

### 2-12. Day1への橋渡し（5分）
1. 2-9で手作業だった項目を3つ書く（例: jar作成、配置、`-cp` 指定）
2. 2-10で手作業だった項目を3つ書く（例: ルーティング、ヘッダ設定、HTML返却）
3. 2-11で先取りしたJava記法が、Day1のどの行で使われるかを3つ書く
4. それぞれがDay1で何に置き換わるかを書く

対応の目安:
- 手動jar管理 -> `pom.xml` + `mvn` の依存管理
- 手動Web起動 -> Spring Boot の自動設定 + 組み込みサーバー
- 手動ルーティング -> `@Controller` + `@GetMapping`

---

## 3. 今日のゴール
- Javaの基本構文を「自分で動かして理解できた」状態になる
- Day1で出るJava記法（アノテーション、コンストラクタ注入、`private final`、`||`/`isBlank`/`trim`、`import static`、`assertEquals`）を読める
- 手動ライブラリ追加と手動Web起動を体験し、Day1で置き換わる部分を説明できる
- Day1のSpring Boot演習に進む準備ができた

---

## 4. つまずきポイント
- `javac` / `java` が見つからない → `JAVA_HOME` と `Path` を確認
- `class X is public, should be declared in a file named X.java`  
  → クラス名とファイル名が一致しているか確認
- `-cp` 指定がない/誤っている → jar利用時はコンパイル時・実行時の両方でクラスパスを確認
- `Address already in use: bind` → 8080ポートを別プロセスが使用中。対象停止かポート変更
- 文字化けや `BOM` 由来のエラーが出る → ファイルを `UTF-8`（BOMなし推奨）で保存し直す

---

## 4-0. エラー早見表（症状→原因→直し方）
| 症状 | 主な原因 | 直し方 |
|---|---|---|
| `javac` / `java` が見つからない | `JAVA_HOME` / `Path` 未設定、またはターミナル再起動前 | 環境変数を設定し、Git Bash を再起動 |
| `class X is public, should be declared in a file named X.java` | クラス名とファイル名不一致 | `public class X` と `X.java` を一致させる |
| `package ... does not exist` | jar利用時の `-cp` 指定漏れ/誤り | コンパイル時・実行時の両方で `-cp` を確認 |
| `Address already in use: bind` | 使用ポートが競合している | 既存プロセス停止、または使用ポートを変更 |
| `reached end of file while parsing` | `}` の閉じ忘れ | `{` と `}` の数を対応させる |
| `';' expected` | 文末の `;` 抜け | エラー行付近の文末に `;` を付ける |
| `cannot find symbol` | 変数名/メソッド名の打ち間違い、`import` 不足 | エラー箇所のスペル確認、必要な `import` を追加 |
| 文字化け、コンパイル時の文字関連エラー | 文字コード不一致（BOM含む） | UTF-8（BOMなし推奨）で保存し直す |

使い方:
1. エラーメッセージの先頭1行を読む
2. この表で近い症状を探す
3. 直し方を実施してから再度 `javac` を実行する

---

## 4-1. Javaミス防止チェックリスト（毎回確認）
- `public class クラス名` と `ファイル名.java` が一致している
- `{` と `}` の数が合っている（ブロック閉じ忘れがない）
- 各文の末尾に `;` を付けている
- 文字列は `"` で囲んでいる（`'` ではない）
- `main` シグネチャが正しい  
  `public static void main(String[] args)`
- 型と値が合っている（`int` に文字列を代入していない）
- 比較は `==` / `>=` など、代入は `=` と使い分けている
- 文字列比較で `.equals(...)` を使っている
- `import` が必要なクラスを忘れていない（List/Map など）
- 実行前に `javac` でコンパイルエラーを先に潰している

---

## 5. 時間割目安（1日）
- 午前: HTML基礎(90分) / CSS基礎(90分) / ミニ制作(30分)
- 午後: Java基礎(120分) / コレクション・メソッド(90分) / ミニ制作(60分)
