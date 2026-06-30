# Java-10 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-10-multi-class-development.md`

## レベル1（基本）解答
`OrderCalculator` に送料込みメソッドを追加:

```java
public int calcTotalWithShipping(OrderItem item, int shippingFee) {
    return calcSubtotal(item) + shippingFee;
}
```

---

## レベル2（拡張）解答
```java
OrderItem item1 = new OrderItem();
item1.productName = "Mouse";
item1.quantity = 2;
item1.unitPrice = 2500;

OrderItem item2 = new OrderItem();
item2.productName = "Keyboard";
item2.quantity = 1;
item2.unitPrice = 5000;

OrderCalculator c = new OrderCalculator();
int total = c.calcSubtotal(item1) + c.calcSubtotal(item2);
System.out.println("2件合計: " + total);
```

---

## レベル3（実務）解答
1. `import model.OrderItem;` を外すと `cannot find symbol`  
2. `import` を戻して再コンパイル  
3. `package model;` を `package models;` に変えると不一致エラー  
4. `package` とフォルダ階層を一致させて復旧

---

## レベル4（実務）解答
- `java app.OrderApp` は失敗
- `java -cp . app.OrderApp` も失敗
- `java -cp out app.OrderApp` は成功

`-cp out` は `out` フォルダをクラス探索の起点にする指定。
今回の `.class` は `out/app/OrderApp.class` にあるため、`-cp out` が必要。

---

## 実行前予想問題の解答
必須行は `1` と `2`。  
`import java.util.List;` はこの課題では不要。
