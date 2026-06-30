# Java-05 ハンズオン: 代表的なクラスライブラリ

## 1. この資料のゴール
- Java標準ライブラリの基本的な使い方を理解する
- `String`, `Math`, `LocalDate`, `UUID` を実務用途で使える
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
1. 標準ライブラリは JDK に含まれてい
2. `import` は `java.lang` 以外のパッケージにあるクラスを短く書くための宣言
3. 便利メソッドを使うと、自作コードを減らせる
4. `isBlank()` は入力チェックで「未入力または空白だけ」の値を判定するときに使う
5. `trim()` はデータ整形で文字列の前後空白を除去し、保存や比較を安定させるときに使う

用語の整理:

| 対象 | パッケージ | この資料での使用箇所 | `import` |
| --- | --- | --- | --- |
| `String`, `trim()`, `isBlank()` | `java.lang` | Step 1, Step 3 | 不要 |
| `Math.round()`, `Math.max()` | `java.lang` | Step 2, Step 3 | 不要 |
| `LocalDate`, `LocalDateTime` | `java.time` | Step 3 | 必要 |
| `UUID` | `java.util` | Step 3 | 必要 |

基本データ型との違い:

| 種類 | 例 | 説明 |
| --- | --- | --- |
| 基本データ型 | `int`, `double`, `boolean`, `char` | Java言語に組み込まれている型。標準ライブラリではない |
| 標準ライブラリのクラス | `String`, `Math`, `LocalDate`, `UUID` | JDKに含まれるクラス |

補足:
- `String` や `Math` も JDK標準ライブラリだが、`java.lang` パッケージは自動的に使えるため `import` しない
- `LocalDate`、`LocalDateTime`、`UUID` も JDK標準ライブラリだが、`java.lang` 以外のパッケージにあるため `import` する
- ここでいう「別パッケージ」は外部ライブラリという意味ではなく、`java.time` や `java.util` のような別の標準パッケージという意味

#### Windows環境でのJDKと標準ライブラリの場所

Windowsでは、JDK は通常 `C:\Program Files\...` 配下にインストールされる。

この環境の例:

```text
C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
```

実際に使われている JDK の場所は、次のコマンドで確認できる。

```bash
java -XshowSettings:properties -version
```

出力の中にある `java.home` が、実行中の Java が使っている JDK の場所を表す。

主なディレクトリ:

| 場所 | 役割 |
| --- | --- |
| `bin` | `java.exe`、`javac.exe` などの実行コマンド |
| `lib\modules` | Java 17 の標準ライブラリ本体が入っている実行用モジュール |
| `jmods` | JDK のモジュールファイル。`java.base.jmod` などがある |
| `lib\src.zip` | 標準ライブラリのソースコード。`String.java` や `LocalDate.java` などを確認できる |

Java 17 では、昔の Java 8 以前で使われていた `rt.jar` ではなく、標準ライブラリはモジュール形式で管理される。

この資料で使うクラスも、すべて JDK に含まれる標準ライブラリである。

今回の演習で使うクラスのソースコードは、JDK 内の `lib\src.zip` に含まれている。

| 演習で使うクラス | ソースコードの場所 |
| --- | --- |
| `String` | `java.base/java/lang/String.java` |
| `Math` | `java.base/java/lang/Math.java` |
| `LocalDate` | `java.base/java/time/LocalDate.java` |
| `LocalDateTime` | `java.base/java/time/LocalDateTime.java` |
| `UUID` | `java.base/java/util/UUID.java` |

```text
java.lang.String
java.lang.Math
java.time.LocalDate
java.time.LocalDateTime
java.util.UUID
```

`String` や `Math` は `java.lang` にあるため `import` なしで使える。  
`LocalDate`、`LocalDateTime`、`UUID` は `java.lang` 以外にあるため `import` が必要。

注意:
- これらの JDK 内部ファイルは、通常は直接編集しない
- 学習では「どこにあるか」を確認し、使い方は Javadoc やコード例で確認する

### Javadocを3分だけ使う

Java-20で本格的に扱う前に、標準クラスの仕様をJavadocで確認する習慣を付けます。

1. [Java 17のString Javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html)を開く
2. ページ内検索で`isBlank`を探す
3. 戻り値が`boolean`であることを確認する
4. 「空文字だけでなく空白文字だけの場合も`true`になる」ことを説明する

ここでは暗記せず、「使い方が不明なときはJavadocのメソッド詳細を見る」ことだけ確認します。

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

期待出力例:
```text
元の文字列: [  Shinesoft  ]
整形後: [Shinesoft]
空白だけか: true
```



### Step 2: Mathクラスを追加
`LibraryDemo.java` を次の内容に更新:

```java
public class LibraryDemo { // Math クラスの利用例
    public static void main(String[] args) {
        int price = 1280; // 税抜価格
        double taxRate = 0.10; // 税率 10%
        int taxed = (int) Math.round(price * (1 + taxRate)); // Math.round(...) は小数を四捨五入して long を返す（.5 以上切り上げ）ため、(int) で型を合わせている
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

期待出力例:
```text
税込価格(四捨五入): 1408
比較結果(大きい方): 1408
```



コード解説:
- `Math.round` は四捨五入
- `Math.max` は大きい方を返す

### Step 3: 日付とIDを追加（仕上げ）
`LibraryDemo.java` を次の内容に更新:

Step 3 で追加する `import`:

| import文 | 使うクラス | パッケージ | この演習での役割 | ソースコードの場所 |
| --- | --- | --- | --- | --- |
| `import java.time.LocalDate;` | `LocalDate` | `java.time` | 今日の日付を取得する | `java.base/java/time/LocalDate.java` |
| `import java.time.LocalDateTime;` | `LocalDateTime` | `java.time` | 現在の日時を取得する | `java.base/java/time/LocalDateTime.java` |
| `import java.util.UUID;` | `UUID` | `java.util` | 重複しにくいIDを生成する | `java.base/java/util/UUID.java` |

補足:
- これらは外部ライブラリではなく、JDK に含まれる標準ライブラリのクラス
- `java.lang` 以外のパッケージにあるため、短い名前で使うには `import` が必要
- `import` しない場合は、`java.time.LocalDate.now()` のように完全修飾名で書く必要がある

```java
import java.time.LocalDate; // 日付のみを扱うクラス
import java.time.LocalDateTime; // 日時を扱うクラス
import java.util.UUID; // 一意な識別子を生成するクラス

public class LibraryDemo { // 代表的な標準ライブラリの利用例
    public static void main(String[] args) {
        String rawName = "  Shinesoft  "; // 先頭と末尾に空白を含む文字列
        String normalized = rawName.trim(); // trim() で前後空白を除去

        int price = 1280; // 税抜価格
        double taxRate = 0.10; // 税率 10%
        int taxed = (int) Math.round(price * (1 + taxRate)); // Math.round(...) は小数を四捨五入して long を返す（.5 以上切り上げ）ため、(int) で型を合わせている
        int max = Math.max(900, taxed); // 900 と taxed の大きい方を取得

        LocalDate today = LocalDate.now(); // 今日の日付を取得
        LocalDateTime now = LocalDateTime.now(); // 現在の日時を取得
        String orderId = UUID.randomUUID().toString(); // ランダムなUUIDを文字列化

        System.out.println("元の文字列: [" + rawName + "]"); // 加工前を表示
        System.out.println("整形後: [" + normalized + "]"); // 加工後を表示
        System.out.println("空白だけか: " + "   ".isBlank()); // isBlank() で空白だけか判定
        System.out.println("税込価格(四捨五入): " + taxed); // 計算結果を表示
        System.out.println("比較結果(大きい方): " + max); // 比較結果を表示
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

期待出力例:
```text
元の文字列: [  Shinesoft  ]
整形後: [Shinesoft]
空白だけか: true
税込価格(四捨五入): 1408
比較結果(大きい方): 1408
営業日: 2026-05-29
処理時刻: 2026-05-29T09:30:15.123456789
注文ID: 123e4567-e89b-12d3-a456-426614174000
```

補足:
- 日付、時刻、UUID は実行する日時や環境によって変わる
- 例として、2026年5月29日に実行した場合は `営業日: 2026-05-29` のように表示される



学習ポイント:
- `java.time` は日付時刻の標準API
- `UUID` は重複しにくい識別子生成に使う

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `LocalDate.now()` を `plusDays(3)` して3日後を表示する。
2. `UUID` を2回生成して値が異なることを確認する。

期待出力例:
```text
3日後: 2026-06-01
UUID-1: 11111111-1111-1111-1111-111111111111
UUID-2: 22222222-2222-2222-2222-222222222222
```

補足:
- 例として、2026年5月29日に実行した場合の3日後は `2026-06-01`
- UUID の値は実行するたびに変わる

### レベル2（拡張）
1. `trim()` 前後の文字列長を `length()` で比較する。
2. `price = 1980`、`taxRate = 0.08` に変更し、`Math.round` の結果が変わることを確認する。

期待出力例:
```text
trim前 length: 13
trim後 length: 9
税込価格(四捨五入): 2138
```

### レベル3（実務）
1. `orderId` の先頭に `"ORD-"` を付けた業務向けIDを作り、`today` と合わせて1行で表示する。

期待出力例:
```text
2026-05-29 / ORD-123e4567-e89b-12d3-a456-426614174000
```

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
- `incompatible types: possible lossy conversion from long to int`
  -> `Math.round(...)` の戻り値は `long`。`(int)` キャストするか変数型を見直す


