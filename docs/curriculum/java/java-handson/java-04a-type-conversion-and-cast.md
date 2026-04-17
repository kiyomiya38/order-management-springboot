# Java-04A 補講: 型変換とキャスト（暗黙変換 / 明示変換）

対応参考資料: `J1-07_データの型変換.pdf`

## 1. この資料のゴール
- 代入時・演算時の型変換ルールを説明できる
- `Integer.parseInt` / `String.valueOf` を使って文字列と数値を変換できる
- キャストの副作用（切り捨て、オーバーフロー）を理解できる

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
1. 代入時は「小さい型 -> 大きい型」は暗黙変換される
2. 演算時は、より大きい型に揃えて計算される
3. 明示キャストは便利だが、値が欠けることがある

---

## 4. ハンズオン

目的:
- 型変換の挙動を実行で確認する

完了条件:
- `TypeConversionDemo.java` の出力から、型変換のルールを説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson04a/TypeConversionDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson04a
cd ~/order-management-springboot/practice/java/handson04a
```

### Step 1: 代入・演算時の暗黙変換を確認する
`TypeConversionDemo.java` を次の内容で作成:

```java
public class TypeConversionDemo {
    public static void main(String[] args) {
        int qty = 3;
        long longQty = qty; // int -> long は暗黙変換

        int price = 1200;
        double taxRate = 0.10;
        double taxed = price * (1 + taxRate); // int と double の演算は double に揃う

        System.out.println("longQty: " + longQty);
        System.out.println("taxed: " + taxed);
    }
}
```

実行:
```bash
javac -encoding UTF-8 TypeConversionDemo.java
java TypeConversionDemo
```

期待結果:
- `longQty: 3` が表示される
- `taxed: 1320.0` が表示される

### Step 2: 文字列と数値を相互変換する
`TypeConversionDemo.java` を次の内容に更新:

```java
public class TypeConversionDemo {
    public static void main(String[] args) {
        String quantityText = "15";
        int quantity = Integer.parseInt(quantityText); // String -> int

        int unitPrice = 800;
        int subtotal = quantity * unitPrice;
        String subtotalText = String.valueOf(subtotal); // int -> String

        System.out.println("quantity(int): " + quantity);
        System.out.println("subtotal(String): " + subtotalText);
    }
}
```

実行:
```bash
javac -encoding UTF-8 TypeConversionDemo.java
java TypeConversionDemo
```

期待結果:
- `quantity(int): 15` が表示される
- `subtotal(String): 12000` が表示される

### Step 3: 明示キャストの挙動を確認する
`TypeConversionDemo.java` を次の内容に更新:

```java
public class TypeConversionDemo {
    public static void main(String[] args) {
        double score = 99.8;
        int scoreInt = (int) score; // 小数点以下は切り捨て

        long bigId = 3_000_000_000L;
        int narrowed = (int) bigId; // 範囲外のため値が崩れる

        System.out.println("scoreInt: " + scoreInt);
        System.out.println("narrowed: " + narrowed);
    }
}
```

実行:
```bash
javac -encoding UTF-8 TypeConversionDemo.java
java TypeConversionDemo
```

期待結果:
- `scoreInt: 99` が表示される
- `narrowed` は `3000000000` ではない値になる

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `int orderCount = 7;` を `double orderCount = 7;` に変え、出力差分を確認する。

期待結果:
- `7.0` のように小数型表現で表示される。

### レベル2（拡張）
1. `"1080"` を `parseInt` して、税率 `0.10` を適用した金額を表示する。

期待結果:
- 計算結果が数値として表示される。

### レベル3（実務）
1. 価格文字列が不正なとき（例: `"10A0"`）に例外が起きることを確認する。
2. `try-catch` で補足して「不正な数値です」と表示する。

期待結果:
- 不正入力時にプログラムが落ちず、メッセージで通知できる。

### 実行前予想問題（1分）
次の2つの出力を実行前に予想してください。
- `System.out.println((int) 12.9);`
- `System.out.println(5 + 2.5);`

### デバッグ演習（任意, 5分）
1. `int x = Integer.parseInt("12A");` を実行して例外を発生させる。
2. どの行で失敗したかスタックトレースを確認する。
3. 文字列を `"12"` に直して再実行する。

---

## 6. つまずきポイント
- `incompatible types`
  -> 代入先と代入元の型を確認
- `NumberFormatException`
  -> `parseInt` の入力文字列が数値のみか確認
- キャスト後の値が想定と違う
  -> 切り捨て・オーバーフローの可能性を確認
