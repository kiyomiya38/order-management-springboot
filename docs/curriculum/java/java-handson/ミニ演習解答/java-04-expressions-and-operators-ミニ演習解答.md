# Java-04 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-04-expressions-and-operators.md`

## レベル1（基本）解答
変更内容:
- `member = false` に変更

```java
boolean member = false;
int discount = member ? 500 : 0;
System.out.println("会員割引: " + discount);
```

期待出力例:
```text
会員割引: 0
```

---

## レベル2（拡張）解答
変更内容:
- `quantity = 10` に変更

```java
int quantity = 10;
int unitPrice = 1800;
int shippingFee = 800;
boolean member = true;

int subtotal = quantity * unitPrice;      // 18000
int discount = member ? 500 : 0;          // 500
int taxed = (subtotal - discount) * 110 / 100; // 19250
int billingAmount = taxed + shippingFee;  // 20050
```

期待出力例:
```text
請求金額: 20050
```

---

## レベル3（実務）解答
変更内容:
- 割引条件を `subtotal >= 5000 ? 1000 : 0` に変更

```java
int discount = subtotal >= 5000 ? 1000 : 0;
```

確認例（`quantity=4`, `unitPrice=1800`）:
- `subtotal = 7200`
- `discount = 1000`

---

## 実行前予想問題の解答
1. `System.out.println(100 + 5 * 2);`
- 出力: `110`

2. `System.out.println((100 + 5) * 2);`
- 出力: `210`

---

## デバッグ演習（任意）の解答
壊した式:
```java
int taxed = (subtotal - discount) * 110 / 100.0;
```

発生エラー例:
- `possible lossy conversion from double to int`

修正例:
```java
int taxed = (subtotal - discount) * 110 / 100;
```
