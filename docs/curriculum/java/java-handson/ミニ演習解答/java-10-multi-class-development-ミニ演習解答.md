# Java-10 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-10-multi-class-development.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 4のパッケージ構成を引き継ぎます。変更する2ファイルを次の全コードへ更新します。`src/model/OrderItem.java`は変更しません。

`src/service/OrderCalculator.java`:

```java
package service;

import model.OrderItem;

public class OrderCalculator {
    public int calcSubtotal(OrderItem item) {
        return item.quantity * item.unitPrice;
    }

    // ===== レベル1で追加: 小計に送料を加える =====
    public int calcTotalWithShipping(OrderItem item, int shippingFee) {
        return calcSubtotal(item) + shippingFee;
    }
    // ===== レベル1で追加ここまで =====
}
```

`src/app/OrderApp.java`:

```java
package app;

import model.OrderItem;
import service.OrderCalculator;

public class OrderApp {
    public static void main(String[] args) {
        OrderItem item = new OrderItem();
        item.productName = "Laptop";
        item.quantity = 2;
        item.unitPrice = 120000;

        OrderCalculator calculator = new OrderCalculator();

        // ===== レベル1で変更: 小計表示を送料込みの請求額表示へ変更 =====
        int billingAmount = calculator.calcTotalWithShipping(item, 800);
        System.out.println(item.productName + " 請求額: " + billingAmount);
        // ===== レベル1で変更ここまで =====
    }
}
```

期待出力:
```text
Laptop 請求額: 240800
```

---

## レベル2（拡張）解答
レベル1の`OrderCalculator.java`はそのまま使用します。`src/app/OrderApp.java`を次の全コードへ更新します。

```java
package app;

import model.OrderItem;
import service.OrderCalculator;

public class OrderApp {
    public static void main(String[] args) {
        OrderItem item = new OrderItem();
        item.productName = "Laptop";
        item.quantity = 2;
        item.unitPrice = 120000;

        OrderCalculator calculator = new OrderCalculator();

        int billingAmount = calculator.calcTotalWithShipping(item, 800);
        System.out.println(item.productName + " 請求額: " + billingAmount);

        // ===== レベル2で追加: 2件目の商品と2件合計を計算する =====
        OrderItem mouse = new OrderItem();
        mouse.productName = "Mouse";
        mouse.quantity = 2;
        mouse.unitPrice = 2500;

        int laptopSubtotal = calculator.calcSubtotal(item);
        int mouseSubtotal = calculator.calcSubtotal(mouse);
        int total = laptopSubtotal + mouseSubtotal;

        System.out.println(item.productName + " 小計: " + laptopSubtotal);
        System.out.println(mouse.productName + " 小計: " + mouseSubtotal);
        System.out.println("2件合計: " + total);
        // ===== レベル2で追加ここまで =====
    }
}
```

期待出力:
```text
Laptop 請求額: 240800
Laptop 小計: 240000
Mouse 小計: 5000
2件合計: 245000
```

---

## レベル3（実務）解答
レベル2の完成コードから一時的に変更して、`package`と`import`の不一致を確認します。通常実行へ戻すため、確認後は必ず元に戻します。

1. `import model.OrderItem;` を外すと、`OrderCalculator` 内の `OrderItem` を解決できず `cannot find symbol` になる。
2. `import model.OrderItem;` を戻して再コンパイルする。
3. `package model;` を `package models;` に変えると、`import model.OrderItem;` と一致しなくなる。
4. `package model;` に戻し、次のコマンドで復旧を確認する。

```bash
javac -encoding UTF-8 -d out src/model/OrderItem.java src/service/OrderCalculator.java src/app/OrderApp.java
java -cp out app.OrderApp
```

---

## レベル4（実務）解答
- `java app.OrderApp` は失敗
- `java -cp . app.OrderApp` も失敗
- `java -cp out app.OrderApp` は成功

`-cp out` は `out` フォルダをクラス探索の起点にする指定。
今回の `.class` は `out/app/OrderApp.class` にあるため、`-cp out` が必要。

---

## 実行前予想問題の解答
Step 4の構成では、必要な行は `1` と `2`。

- `package app;` は `OrderApp` が `app` パッケージに属することを宣言する
- `import service.OrderCalculator;` は別パッケージの `OrderCalculator` を短いクラス名で使うために必要
- `import java.util.List;` はこのプログラムでは `List` を使用しないため不要
