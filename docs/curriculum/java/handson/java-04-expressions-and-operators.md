# Java-04 ハンズオン: 式と演算子（実務で使う計算・判定）

対応参考資料: `Java-04_式と演算子.pptx`

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

---

## 3. 先に覚えるポイント
1. 式は「値を作る」もの（例: `quantity * unitPrice`）
2. `==`, `!=`, `>`, `>=` は比較（結果は `boolean`）
3. `&&`, `||`, `!` は論理演算（条件の組み合わせ）

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
public class OperatorDemo {
    public static void main(String[] args) {
        int quantity = 3;
        int unitPrice = 1200;

        int subtotal = quantity * unitPrice;   // 乗算
        int plus = subtotal + 500;             // 加算
        int minus = subtotal - 200;            // 減算
        int divide = subtotal / 3;             // 除算（整数）
        int mod = subtotal % 7;                // 余り

        System.out.println("小計: " + subtotal);
        System.out.println("加算結果: " + plus);
        System.out.println("減算結果: " + minus);
        System.out.println("除算結果: " + divide);
        System.out.println("余り: " + mod);
    }
}
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: 比較・論理演算子で判定する
`OperatorDemo.java` を次の内容に更新:

```java
public class OperatorDemo {
    public static void main(String[] args) {
        int quantity = 3;
        int unitPrice = 1200;
        int subtotal = quantity * unitPrice;
        boolean paid = false;

        boolean isHighAmount = subtotal >= 3000;
        boolean canShip = isHighAmount && paid;
        boolean needsReview = subtotal > 5000 || quantity >= 10;

        System.out.println("高額注文か: " + isHighAmount);
        System.out.println("出荷可能か: " + canShip);
        System.out.println("審査が必要か: " + needsReview);
    }
}
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


コード解説:
- `subtotal >= 3000` は比較式（`boolean` になる）
- `&&` は両方 `true` のときだけ `true`
- `||` はどちらか一方が `true` なら `true`

### Step 3: 優先順位（括弧）の違いを確認する
`OperatorDemo.java` を次の内容に更新:

```java
public class OperatorDemo {
    public static void main(String[] args) {
        int a = 4 + 5 * 6;      // 4 + (5 * 6)
        int b = (4 + 5) * 6;    // 括弧を優先

        System.out.println("4 + 5 * 6 = " + a);
        System.out.println("(4 + 5) * 6 = " + b);
    }
}
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
public class OperatorDemo {
    public static void main(String[] args) {
        int quantity = 4;
        int unitPrice = 1800;
        int shippingFee = 800;
        boolean member = true;

        int subtotal = quantity * unitPrice;
        int discount = member ? 500 : 0; // 三項演算子
        int taxed = (subtotal - discount) * 110 / 100;
        int billingAmount = taxed + shippingFee;

        System.out.println("小計: " + subtotal);
        System.out.println("会員割引: " + discount);
        System.out.println("税込金額: " + taxed);
        System.out.println("請求金額: " + billingAmount);
    }
}
```

実行:
```bash
javac -encoding UTF-8 OperatorDemo.java
java OperatorDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `member` を `false` にして割引額を確認
2. `quantity` を `10` に変えたときの請求金額を確認
3. `discount` を「`subtotal >= 5000` のとき 1000」に変更

---

## 6. つまずきポイント
- `bad operand types`
  -> 演算子の左右の型を確認
- `boolean` と数値を混在させた計算をしてしまう
  -> 条件式と算術式を分ける
- 括弧忘れで意図しない計算になる
  -> 業務ロジックは括弧で明示する
