# Java-11 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-11-class-mechanisms.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
`Product` にフィールドを追加する。

```java
int quantity;
```

コンストラクタを変更する。

```java
Product(String name, int price, int quantity) {
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    createdCount++;
}
```

Step 4の生成処理を変更し、`main(...)`の既存表示処理より後へ数量の表示を追加する。

```java
Product p1 = new Product("Keyboard", 5000, 2);
Product p2 = new Product("Mouse", 2500, 5);
System.out.println(p1.name + " quantity: " + p1.quantity);
```

期待出力:
```text
Keyboard quantity: 2
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継いで実施する。`quantity`フィールドと3引数コンストラクタは残す。
`PriceUtil` に次のメソッドを追加する。

```java
static int calcDiscounted(int basePrice, int discountRatePercent) {
    return basePrice * (100 - discountRatePercent) / 100;
}
```

`main(...)`の既存表示処理より後から呼び出し、戻り値を演習で指定した`discounted`へ代入する。

```java
int discounted = PriceUtil.calcDiscounted(p1.price, 10);
System.out.println("割引後価格: " + discounted);
```

期待出力:
```text
割引後価格: 4500
```

---

## レベル3（実務）解答
レベル2の2件生成後、`Product.createdCount`を表示する前へ3件目を追加する。

```java
Product p3 = new Product("Monitor", 30000, 1);
```

Step 4にある `Product.createdCount` の表示処理は変更せずに使用する。

期待出力:
```text
作成件数: 3
```

---

## 実行前予想問題の解答
Step 4ですでに2件生成されているため、3件目の生成後は`3`になる。

---

## デバッグ演習（任意）の解答
`calcTaxIncluded(...)` には `int basePrice` が必要なため、引数なしでは呼び出せない。

`p1.price` を渡す呼び出しへ戻す。

```java
PriceUtil.calcTaxIncluded(p1.price)
```
