# Java-05 ハンズオン: 代表的なクラスライブラリ

対応参考資料: `Java-05_代表的なクラスライブラリ.pptx`

## 1. この資料のゴール
- Java標準ライブラリの基本的な使い方を理解する
- `String`, `Math`, `LocalDate`, `UUID` を実務用途で使える
- `Path` と `Pattern` の基本用途を説明できる（Webアプリ先読み）
- `import` の意味を説明できる

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

---

## 3. 先に覚えるポイント
1. 標準ライブラリは JDK に含まれている
2. `import` は別パッケージのクラスを短く書くための宣言
3. 便利メソッドを使うと、自作コードを減らせる
4. `Path` はファイルパス、`Pattern` は正規表現パターンを表す型
5. `private static final` は「クラス内で共有し、再代入しない定数」の定番宣言

---

## 4. ハンズオン

目的:
- 実務で頻出の標準クラスを使う

完了条件:
- 文字列整形、数学計算、日付取得、ID生成を1つのプログラムで実行できる

作成ファイル: `~/order-management-springboot/practice/java/handson05/LibraryDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson05
cd ~/order-management-springboot/practice/java/handson05
```

### Step 1: Stringメソッドを使う
`LibraryDemo.java` を次の内容で作成:

```java
public class LibraryDemo { // 標準ライブラリの利用例をまとめるクラス
    public static void main(String[] args) { // 実行開始地点
        String rawName = "  Shinesoft  "; // 先頭と末尾に空白を含む文字列
        String normalized = rawName.trim(); // trim() で前後空白を除去

        System.out.println("元の文字列: [" + rawName + "]"); // 加工前を表示
        System.out.println("整形後: [" + normalized + "]"); // 加工後を表示
        System.out.println("空白だけか: " + "   ".isBlank()); // isBlank() で空白だけか判定
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: Mathクラスを追加
`LibraryDemo.java` を次の内容に更新:

```java
public class LibraryDemo { // Math クラスの利用例
    public static void main(String[] args) {
        int price = 1280; // 税抜価格
        double taxRate = 0.10; // 税率 10%
        int taxed = (int) Math.round(price * (1 + taxRate)); // 税込価格を四捨五入して int 化
        int max = Math.max(900, taxed); // 900 と taxed の大きい方を取得

        System.out.println("税込価格(四捨五入): " + taxed); // 計算結果を表示
        System.out.println("比較結果(大きい方): " + max); // 比較結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


コード解説:
- `Math.round` は四捨五入
- `Math.max` は大きい方を返す

### Step 3: 日付とIDを追加（仕上げ）
`LibraryDemo.java` を次の内容に更新:

```java
import java.time.LocalDate; // 日付のみを扱うクラス
import java.time.LocalDateTime; // 日時を扱うクラス
import java.util.UUID; // 一意な識別子を生成するクラス

public class LibraryDemo { // 日付とID生成の利用例
    public static void main(String[] args) {
        LocalDate today = LocalDate.now(); // 今日の日付を取得
        LocalDateTime now = LocalDateTime.now(); // 現在の日時を取得
        String orderId = UUID.randomUUID().toString(); // ランダムなUUIDを文字列化

        System.out.println("営業日: " + today); // 日付を表示
        System.out.println("処理時刻: " + now); // 日時を表示
        System.out.println("注文ID: " + orderId); // 生成したIDを表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


学習ポイント:
- `java.time` は日付時刻の標準API
- `UUID` は重複しにくい識別子生成に使う

### Step 4: Webアプリ先読み（`Path` / `Pattern`）(5〜10分)
`LibraryDemo.java` を次の内容に更新:

```java
import java.nio.file.Path; // パス情報を扱う型
import java.util.regex.Matcher; // 正規表現の検索結果を扱う型
import java.util.regex.Pattern; // 正規表現パターンを表す型

public class LibraryDemo { // Path と Pattern の基本利用例
    private static final Path STATIC_DIR = Path.of("static"); // クラス共通で使うディレクトリ定数
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\""); // "name" の値を抽出する正規表現

    public static void main(String[] args) {
        String body = "{\"name\":\"Tanaka\"}"; // 擬似的なJSON文字列
        Matcher matcher = NAME_PATTERN.matcher(body); // body に対して正規表現マッチャーを作成
        String name = ""; // 抽出結果を入れる変数（初期値は空文字）
        if (matcher.find()) { // パターンに一致する箇所があるか確認
            name = matcher.group(1); // 1番目のキャプチャグループ（name値）を取得
        }

        System.out.println("static dir: " + STATIC_DIR); // Path の値を表示
        System.out.println("name: " + name); // 抽出した name を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 LibraryDemo.java
java LibraryDemo
```

期待出力:
```text
static dir: static
name: Tanaka
```

コード解説:
- `Path` はファイル/ディレクトリの場所を安全に扱うための型
- `Pattern` は正規表現を再利用しやすい形にした型
- `Matcher` は `Pattern` を使って文字列を検索する実行オブジェクト
- `private` は「このクラスの外から直接アクセスさせない」
- `static` は「インスタンスごとではなくクラスで1つ共有する」
- `final` は「一度代入した参照を再代入しない」
- つまり `private static final Path STATIC_DIR ...` は「クラス専用の共有定数」を表す

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `LocalDate.now()` を `plusDays(3)` して3日後を表示する。
2. `UUID` を2回生成して値が異なることを確認する。

期待結果:
- 今日の日付より3日後が表示される。
- 2つのUUIDが一致しない。

### レベル2（拡張）
1. `trim()` 前後の文字列長を `length()` で比較する。
2. `body` を `{"name":"Suzuki"}` に変更し、抽出結果が変わることを確認する。

期待結果:
- `trim()` 後の長さが短くなる。
- 抽出される `name` が `Suzuki` になる。

### レベル3（実務）
1. `STATIC_DIR` の宣言から `final` を外し、再代入して挙動の違いを確認する（確認後は元に戻す）。

期待結果:
- `final` ありでは再代入できない。
- `final` なしでは再代入できる。

### 実行前予想問題（1分）
次の結果を実行前に予想してください。
- `System.out.println("  ABC  ".trim().length());`
- `System.out.println("ABC".length());`

### デバッグ演習（任意, 5分）
1. `import java.time.LocalDate;` を一時的に削除してコンパイルする。
2. `cannot find symbol` を確認したら `import` を戻す。
3. 再コンパイルして成功を確認する。

---

## 6. つまずきポイント
- `cannot find symbol`（`LocalDate` など）
  -> `import` 文を確認
- `NullPointerException`
  -> `null` の文字列にメソッドを呼んでいないか確認
- 日付/時刻の型を混同
  -> 日付のみは `LocalDate`、日時は `LocalDateTime`
- 正規表現がマッチしない
  -> `\"` や `\\s*` などのエスケープ記法を確認

