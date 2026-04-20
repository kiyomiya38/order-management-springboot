# Java-05 ハンズオン: 代表的なクラスライブラリ

対応参考資料: `Java-05_代表的なクラスライブラリ.pptx`

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
1. 標準ライブラリは JDK に含まれている
2. `import` は別パッケージのクラスを短く書くための宣言
3. 便利メソッドを使うと、自作コードを減らせる
4. `isBlank()` は入力チェックで「未入力または空白だけ」の値を判定するときに使う
5. `trim()` はデータ整形で文字列の前後空白を除去し、保存や比較を安定させるときに使う

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

期待出力例:
```text
営業日: 2026-04-20
処理時刻: 2026-04-20T09:30:15.123456789
注文ID: 123e4567-e89b-12d3-a456-426614174000
```



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
3日後: 2026-04-23
UUID-1: 11111111-1111-1111-1111-111111111111
UUID-2: 22222222-2222-2222-2222-222222222222
```

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
2026-04-20 / ORD-123e4567-e89b-12d3-a456-426614174000
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



