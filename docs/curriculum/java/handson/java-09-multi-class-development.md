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

期待状態:
- `java -version` と `javac -version` の両方で `17` が表示される
- 例: `17.0.x`

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
public class OrderItem { // 注文1件分のデータを保持するクラス
    String productName; // 商品名
    int quantity; // 数量
    int unitPrice; // 単価
} // クラス定義の終わり
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
public class OrderCalculator { // 注文金額を計算するクラス
    int calcSubtotal(OrderItem item) { // OrderItem を受け取り小計を返す
        return item.quantity * item.unitPrice; // 数量 x 単価を計算
    }
} // クラス定義の終わり
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
public class OrderApp { // 実行クラス（エントリーポイント）
    public static void main(String[] args) {
        OrderItem item = new OrderItem(); // 注文データ用インスタンスを作成
        item.productName = "Laptop"; // 商品名を設定
        item.quantity = 2; // 数量を設定
        item.unitPrice = 120000; // 単価を設定

        OrderCalculator calculator = new OrderCalculator(); // 計算クラスを生成
        int subtotal = calculator.calcSubtotal(item); // 小計を計算

        System.out.println(item.productName + " 小計: " + subtotal); // 結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
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

### Step 4: package と import を使って実行する（仕上げ）
作成フォルダ:
```bash
mkdir -p src/model src/service src/app out
```

作成ファイル: `src/model/OrderItem.java`
```java
package model; // model パッケージに属することを宣言

public class OrderItem { // 注文データを表すクラス
    public String productName; // 商品名
    public int quantity; // 数量
    public int unitPrice; // 単価
} // クラス定義の終わり
```

作成ファイル: `src/service/OrderCalculator.java`
```java
package service; // service パッケージに属することを宣言

import model.OrderItem; // model パッケージの OrderItem を利用

public class OrderCalculator { // 金額計算を担当するサービスクラス
    public int calcSubtotal(OrderItem item) { // 注文データを受け取り小計を返す
        return item.quantity * item.unitPrice; // 数量 x 単価を計算
    }
} // クラス定義の終わり
```

作成ファイル: `src/app/OrderApp.java`
```java
package app; // app パッケージに属することを宣言

import model.OrderItem; // model パッケージのクラスを利用
import service.OrderCalculator; // service パッケージのクラスを利用

public class OrderApp { // パッケージ構成版の実行クラス
    public static void main(String[] args) {
        OrderItem item = new OrderItem(); // 注文データを生成
        item.productName = "Laptop"; // 商品名を設定
        item.quantity = 2; // 数量を設定
        item.unitPrice = 120000; // 単価を設定

        OrderCalculator calculator = new OrderCalculator(); // 計算クラスを生成
        int subtotal = calculator.calcSubtotal(item); // 小計を計算
        System.out.println(item.productName + " 小計: " + subtotal); // 結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり
```

実行:
```bash
javac -encoding UTF-8 -d out src/model/OrderItem.java src/service/OrderCalculator.java src/app/OrderApp.java
java -cp out app.OrderApp
```

期待出力:
```text
Laptop 小計: 240000
```


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

