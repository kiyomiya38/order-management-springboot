# Java-08 ハンズオン: メソッド

対応参考資料: `Java-08_メソッド.pptx`

## 1. この資料のゴール
- メソッドの定義と呼び出しを理解する
- 引数と戻り値を使って再利用可能な処理を作れる
- オーバーロードの基本を説明できる

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. メソッドは「処理の部品」
2. 引数は入力、戻り値は出力
3. 同名メソッドでも引数が違えば共存できる（オーバーロード）

---

## 4. ハンズオン

目的:
- 計算ロジックをメソッドに分離する

完了条件:
- `MethodDemo.java` で引数・戻り値・オーバーロードを確認できる

作成ファイル: `~/order-management-springboot/practice/java/handson08/MethodDemo.java`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson08
cd ~/order-management-springboot/practice/java/handson08
```

### Step 1: 引数なしメソッド
`MethodDemo.java` を次の内容で作成:

```java
public class MethodDemo {
    static void printStartMessage() {
        System.out.println("受注処理を開始します");
    }

    public static void main(String[] args) {
        printStartMessage();
    }
}
```

実行:
```bash
javac -encoding UTF-8 MethodDemo.java
java MethodDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 2: 引数と戻り値を追加
`MethodDemo.java` を次の内容に更新:

```java
public class MethodDemo {
    static int calcTotal(int quantity, int unitPrice) {
        return quantity * unitPrice;
    }

    public static void main(String[] args) {
        int total = calcTotal(3, 1200);
        System.out.println("合計: " + total);
    }
}
```

実行:
```bash
javac -encoding UTF-8 MethodDemo.java
java MethodDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


コード解説:
- `int quantity, int unitPrice` は仮引数（メソッド内で使う受け口）
- `return` は呼び出し元へ値を返す

### Step 3: オーバーロードを追加
`MethodDemo.java` を次の内容に更新:

```java
public class MethodDemo {
    static int calcTotal(int quantity, int unitPrice) {
        return quantity * unitPrice;
    }

    static int calcTotal(int quantity, int unitPrice, int shippingFee) {
        return quantity * unitPrice + shippingFee;
    }

    public static void main(String[] args) {
        int total1 = calcTotal(3, 1200);
        int total2 = calcTotal(3, 1200, 800);

        System.out.println("送料なし合計: " + total1);
        System.out.println("送料込み合計: " + total2);
    }
}
```

実行:
```bash
javac -encoding UTF-8 MethodDemo.java
java MethodDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


### Step 4: 実務メソッドへ仕上げる
`MethodDemo.java` を次の内容に更新:

```java
public class MethodDemo {
    static int calcSubtotal(int quantity, int unitPrice) {
        return quantity * unitPrice;
    }

    static int calcBillingAmount(int quantity, int unitPrice, int shippingFee, int discount) {
        int subtotal = calcSubtotal(quantity, unitPrice);
        return subtotal + shippingFee - discount;
    }

    public static void main(String[] args) {
        int billingAmount = calcBillingAmount(4, 1800, 800, 500);
        System.out.println("請求金額: " + billingAmount);
    }
}
```

実行:
```bash
javac -encoding UTF-8 MethodDemo.java
java MethodDemo
```

期待結果:
- `javac` がエラーなく完了する
- `java` の実行結果が、このStepのコード内容と一致する


---

## 5. ミニ演習（10分）
1. `calcBillingAmount` に `taxRatePercent` 引数を追加
2. `quantity` が0以下なら `0` を返すガードを追加
3. `printStartMessage` を `printStartMessage(String jobName)` に変更

---

## 6. つまずきポイント
- `non-static method ... cannot be referenced from a static context`
  -> `main` から呼ぶメソッドを `static` にするか、インスタンス化する
- 戻り値型と `return` 値の不一致
  -> 宣言型を見直す
- 引数順序のミス
  -> 呼び出し側の順番をコメントで明示
