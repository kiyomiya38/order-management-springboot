# Java-09 ハンズオン: 複数クラスを用いた開発

対応参考資料: `Java-09_複数クラスを用いた開発.pptx`

## 1. この資料のゴール
- クラスを責務ごとに分割できる
- 複数 `.java` ファイルをコンパイル・実行できる
- `package` と `import` の最小形を理解する

---

## 2. 事前準備
```bash
cd ~/order-management-springboot/practice/java
java -version
javac -version
```

---

## 3. 先に覚えるポイント
1. 1クラス1責務に分けると保守しやすい
2. 同一パッケージなら `import` なしで相互参照できる
3. 別パッケージを使うときは `package` と `import` を揃える

---

## 4. ハンズオン

目的:
- 複数クラス連携の基本を体験する

完了条件:
- `OrderItem` / `OrderCalculator` / `OrderApp` の3クラスで実行できる

作成フォルダ: `~/order-management-springboot/practice/java/handson09`

### Step 0: 作業フォルダを作る
```bash
mkdir -p ~/order-management-springboot/practice/java/handson09
cd ~/order-management-springboot/practice/java/handson09
```

### Step 1: データクラスを作る
作成ファイル: `OrderItem.java`

```java
public class OrderItem {
    String productName;
    int quantity;
    int unitPrice;
}
```

コンパイル確認:
```bash
javac -encoding UTF-8 OrderItem.java
```

期待結果:
- コンパイルが成功する（エラーなし）
- `.class` ファイルが生成される


### Step 2: 計算クラスを作る
作成ファイル: `OrderCalculator.java`

```java
public class OrderCalculator {
    int calcSubtotal(OrderItem item) {
        return item.quantity * item.unitPrice;
    }
}
```

コンパイル確認:
```bash
javac -encoding UTF-8 OrderItem.java OrderCalculator.java
```

期待結果:
- コンパイルが成功する（エラーなし）
- `.class` ファイルが生成される


### Step 3: 実行クラスを作る
作成ファイル: `OrderApp.java`

```java
public class OrderApp {
    public static void main(String[] args) {
        OrderItem item = new OrderItem();
        item.productName = "Laptop";
        item.quantity = 2;
        item.unitPrice = 120000;

        OrderCalculator calculator = new OrderCalculator();
        int subtotal = calculator.calcSubtotal(item);

        System.out.println(item.productName + " 小計: " + subtotal);
    }
}
```

実行:
```bash
javac -encoding UTF-8 OrderItem.java OrderCalculator.java OrderApp.java
java OrderApp
```

期待出力:
```text
Laptop 小計: 240000
```

### Step 4: package と import を体験する（任意）
作成フォルダ:
```bash
mkdir -p src/model src/service src/app
```

確認:
```bash
find src -maxdepth 2 -type d
```

期待結果:
- 作成したディレクトリが表示される


学習ポイント:
- 実務では `package` を使って構造化する
- 同じ考え方は Spring Boot の `domain/service/controller` に直結する

---

## 5. ミニ演習（10分）
1. `OrderCalculator` に送料込み計算メソッドを追加
2. `OrderItem` を2件作って合計表示
3. `productName` を `"Mouse"` に変更して確認

---

## 6. つまずきポイント
- `cannot find symbol class OrderItem`
  -> ファイル名・クラス名・コンパイル対象を確認
- 実行クラスが見つからない
  -> `java OrderApp` でクラス名を正確に指定
- パッケージ導入時にエラー
  -> `package` 宣言とフォルダ階層を一致させる
