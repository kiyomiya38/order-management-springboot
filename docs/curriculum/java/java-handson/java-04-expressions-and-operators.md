# Java-04 ハンズオン: 式と演算子（実務で使う計算・判定）

補講（任意）: [Java-04A 型変換とキャスト](./java-04a-type-conversion-and-cast.md)

## 1. この資料のゴール
- 算術演算子・比較演算子・論理演算子を使い分けできる
- 括弧による優先順位の制御を説明できる
- 実務で出る「条件判定 + 金額計算」を1つのプログラムで記述できる

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
1. 式は「値を作る」もの（例: `quantity * unitPrice`）
2. `==`, `!=`, `>`, `>=` は比較（結果は `boolean`）
3. `&&`, `||`, `!` は論理演算（条件の組み合わせ）

実務でよく使う演算子（優先）:

| 区分 | 主な演算子 | 主な用途 | 例 | 結果型 |
|---|---|---|---|---|
| 算術演算 | `+`, `-`, `*`, `/`, `%` | 金額・件数・余りの計算 | `subtotal = quantity * unitPrice` | 数値型（`int` など） |
| 比較演算 | `==`, `!=`, `>`, `>=`, `<`, `<=` | 条件判定（閾値チェック等） | `isHighAmount = subtotal >= 3000` | `boolean` |
| 論理演算 | <code>&amp;&amp;</code>, <code>&#124;&#124;</code>, <code>!</code> | 複数条件の組み合わせ（出荷可否・審査要否などの業務判定） | `canShip = isHighAmount && paid` | `boolean` |
| 三項演算 | `?:` | 条件に応じた値の切り替え | `discount = member ? 500 : 0` | 式の型に依存 |

---

## 4. ハンズオン

目的:
- 演算子を使って業務ルールを表現する

完了条件:
- `OperatorDemo.java` を実行し、計算結果と判定結果を表示できる
- 括弧あり/なしで結果が変わることを説明できる

作成ファイル: `~/order-management-springboot/practice/java/handson04/OperatorDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson04
cd ~/order-management-springboot/practice/java/handson04
```

### Step 1: 算術演算子を確認する
`OperatorDemo.java` を次の内容で作成:

```java
public class OperatorDemo { // クラス宣言。ファイル名は OperatorDemo.java にする
    public static void main(String[] args) { // 実行開始地点
        int quantity = 3; // 数量
        int unitPrice = 1200; // 単価

        int subtotal = quantity * unitPrice; // 乗算: 小計 = 数量 x 単価
        int plus = subtotal + 500; // 加算: 小計に 500 を足す
        int minus = subtotal - 200; // 減算: 小計から 200 を引く
        int divide = subtotal / 3; // 除算: int 同士なので小数点以下は切り捨て
        int mod = subtotal % 7; // 剰余: 7 で割った余り

        System.out.println("小計: " + subtotal); // 小計を表示
        System.out.println("加算結果: " + plus); // 加算結果を表示
        System.out.println("減算結果: " + minus); // 減算結果を表示
        System.out.println("除算結果: " + divide); // 除算結果を表示
        System.out.println("余り: " + mod); // 余りを表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待出力例:
```text
小計: 3600
加算結果: 4100
減算結果: 3400
除算結果: 1200
余り: 2
```



### Step 2: 比較・論理演算子で判定する
`OperatorDemo.java` を次の内容に更新:

```java
public class OperatorDemo { // 比較演算子と論理演算子を使った判定例
    public static void main(String[] args) {
        int quantity = 3; // 数量
        int unitPrice = 1200; // 単価
        int subtotal = quantity * unitPrice; // 小計
        boolean paid = false; // 支払状態

        boolean isHighAmount = subtotal >= 3000; // 小計が 3000 以上なら true
        boolean canShip = isHighAmount && paid; // 高額条件と支払済みの両方を満たす場合のみ true
        boolean needsReview = subtotal > 5000 || quantity >= 10; // 高額または大量注文なら true

        System.out.println("高額注文か: " + isHighAmount); // 比較結果を表示
        System.out.println("出荷可能か: " + canShip); // 論理積 (&&) の結果を表示
        System.out.println("審査が必要か: " + needsReview); // 論理和 (||) の結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待出力例:
```text
高額注文か: true
出荷可能か: false
審査が必要か: false
```



コード解説:
- `subtotal >= 3000` は比較式（`boolean` になる）
- `&&` は両方 `true` のときだけ `true`
- `||` はどちらか一方が `true` なら `true`

### Step 3: 優先順位（括弧）の違いを確認する
`OperatorDemo.java` を次の内容に更新:

```java
public class OperatorDemo { // 演算子の優先順位を確認する例
    public static void main(String[] args) {
        int a = 4 + 5 * 6; // 乗算が先に計算されるため 4 + (5 * 6)
        int b = (4 + 5) * 6; // 括弧内を先に計算してから乗算する

        System.out.println("4 + 5 * 6 = " + a); // 括弧なしの結果を表示
        System.out.println("(4 + 5) * 6 = " + b); // 括弧ありの結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待出力:
```text
4 + 5 * 6 = 34
(4 + 5) * 6 = 54
```

### Step 4: 実務計算にまとめる（仕上げ）
`OperatorDemo.java` を次の内容に更新:

```java
public class OperatorDemo { // 演算子を組み合わせた実務風の金額計算
    public static void main(String[] args) {
        int quantity = 4; // 数量
        int unitPrice = 1800; // 単価
        int shippingFee = 800; // 送料
        boolean member = true; // 会員フラグ

        int subtotal = quantity * unitPrice; // 小計 = 数量 x 単価
        int discount = member ? 500 : 0; // 三項演算子: 会員なら 500 円引き、非会員なら 0
        int taxed = (subtotal - discount) * 110 / 100; // 割引後金額に 10% 税を加算
        int billingAmount = taxed + shippingFee; // 最終請求 = 税込金額 + 送料

        System.out.println("小計: " + subtotal); // 小計を表示
        System.out.println("会員割引: " + discount); // 割引額を表示
        System.out.println("税込金額: " + taxed); // 税込金額を表示
        System.out.println("請求金額: " + billingAmount); // 最終請求金額を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待出力例:
```text
小計: 7200
会員割引: 500
税込金額: 7370
請求金額: 8170
```


補足（実務）:
- この章の金額計算は学習用に `int` を使用している
- 実務の金額計算では端数誤差を避けるため `BigDecimal` を使う


---

## 5. ミニ演習（10分）
### レベル1（基本）
1. `member` を `false` にして割引額を確認する。

期待出力例:
```text
会員割引: 0
請求金額: 8720
```

### レベル2（拡張）
1. `quantity` を `10` に変えたときの請求金額を確認する。

期待出力例:
```text
小計: 18000
請求金額: 20050
```

### レベル3（実務）
1. `discount` を「`subtotal >= 5000` のとき `1000`、それ以外 `0`」に変更する。

期待出力例:
```text
会員割引: 1000
会員割引: 0
```

### 実行前予想問題（1分）
次の2つの出力を、実行前に紙やメモで予想してから確認してください。
- `System.out.println(100 + 5 * 2);`
- `System.out.println((100 + 5) * 2);`

### デバッグ演習（任意, 5分）
1. Step 4 の `taxed` 計算式を次のように意図的に壊す。  
   `int taxed = (subtotal - discount) * 110 / 100.0;`
2. `javac` のエラーメッセージを読み、型不一致を修正する。
3. 修正後に再コンパイルし、元の期待結果に戻ることを確認する。

---

## 6. つまずきポイント
- `bad operand types`
  -> 演算子の左右の型を確認
- `boolean` と数値を混在させた計算をしてしまう
  -> 条件式と算術式を分ける
- 括弧忘れで意図しない計算になる
  -> 業務ロジックは括弧で明示する



