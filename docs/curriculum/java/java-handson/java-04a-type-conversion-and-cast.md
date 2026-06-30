# Java-04A 補講: 型変換とキャスト（暗黙変換 / 明示変換）

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

### 型変換の方向

```mermaid
flowchart LR
  subgraph AUTO["暗黙的な型変換（自動）：左から右"]
    direction LR
    INT1["int<br/>3"] -->|自動変換| LONG1["long<br/>3"]
    LONG1 -->|自動変換| DOUBLE1["double<br/>3.0"]
  end

  subgraph CAST["明示的な型変換（キャスト）：右から左"]
    direction RL
    DOUBLE2["double<br/>9.8"] -->|int へキャスト| INT2["int<br/>9<br/>小数部分を切り捨て"]
    LONG2["long<br/>3,000,000,000"] -->|int へキャスト| INT3["int<br/>値が崩れる可能性"]
  end

  AUTO ~~~ CAST
```

ポイント:
- `int` から `long` や `double` のように、より広い範囲を扱える型へは自動変換される
- 暗黙変換でも、非常に大きい `long` を `double` にすると精度が落ちる場合がある
- `double` や `long` から `int` へ変換するときは、`(int)` のように変換先の型を明示する
- 小さい型へのキャストでは、小数部分の切り捨てやオーバーフローが起こる場合がある
- `String` と数値の変換はキャストではなく、`Integer.parseInt` や `String.valueOf` を使う

### 書式の基本

#### 代入時の暗黙変換

```java
int qty = 3;
long longQty = qty;
```

ポイント:
- `int` から `long` のように、表せる範囲が広い型へは自動変換される
- 逆方向の `long` から `int` は自動では代入できない

#### 演算時の型変換

```java
int price = 1200;
double taxRate = 0.10;
double taxed = price * (1 + taxRate);
```

ポイント:
- `int` と `double` を一緒に計算すると、結果は `double` に揃う
- 小数を含む計算結果を受ける変数は `double` にする

#### 文字列から数値へ変換する

```java
String quantityText = "15";
int quantity = Integer.parseInt(quantityText);
```

ポイント:
- `Integer.parseInt(...)` は数値文字列を `int` に変換する
- `"15"` は変換できるが、`"abc"` は変換できず例外になる

#### 数値から文字列へ変換する

```java
int subtotal = 12000;
String subtotalText = String.valueOf(subtotal);
```

ポイント:
- `String.valueOf(...)` は値を文字列へ変換する
- 画面表示やログ用の文字列を作るときに使える

#### 明示キャスト

```java
double average = 9.8;
int roundedDown = (int) average;
```

ポイント:
- `(int)` のように型を指定して変換することをキャストと呼ぶ
- `double` から `int` へキャストすると、小数部分は切り捨てられる
- 範囲外の値を小さい型へキャストすると、意図しない値になることがある

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

期待出力例:
```text
longQty: 3
taxed: 1320.0
```

### Step 2: 文字列と数値を相互変換する
前のコード全体を置き換え、`TypeConversionDemo.java` を次の内容に更新:

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

期待出力例:
```text
quantity(int): 15
subtotal(String): 12000
```

### Step 3: 明示キャストの挙動を確認する
前のコード全体を置き換え、`TypeConversionDemo.java` を次の内容に更新:

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

期待出力例:
```text
scoreInt: 99
narrowed: -1294967296
```

確認ポイント:
- `double` から `int` へのキャストでは、小数点以下が切り捨てられる
- `long` から `int` へのキャストでは、`int` の範囲を超えると値が崩れる

---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `int orderCount = 7;` を `double orderCount = 7;` に変え、出力差分を確認する。

期待出力例:
```text
7.0
```

### レベル2（拡張）
1. `"1080"` を `parseInt` して、税率 `0.10` を適用した金額を表示する。

期待状態:
- 計算結果が数値として表示される。

### レベル3（実務）
1. 価格文字列が不正なとき（例: `"10A0"`）に例外が起きることを確認する。
2. `try-catch` で捕捉して「不正な数値です」と表示する。

補足:
- 例外処理は Java-17 で詳しく扱う
- ここでは「数値ではない文字列を `parseInt` すると失敗する」「失敗を `catch` で受け取れる」ことだけ確認する

期待出力例:
```text
不正な数値です
```

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
