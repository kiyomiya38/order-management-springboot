# Java-04 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-04-expressions-and-operators.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 4の完成コードから`member`だけを変更し、その他の処理は残す。
変更内容:
- `member = false` に変更

```java
boolean member = false;
int discount = member ? 500 : 0;
System.out.println("会員割引: " + discount);
```

期待出力例:
```text
小計: 7200
会員割引: 0
税込金額: 7920
請求金額: 8720
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、`quantity`だけを変更する。
変更内容:
- `quantity = 10` に変更

```java
int quantity = 10;
int unitPrice = 1800;
int shippingFee = 800;
boolean member = false;

int subtotal = quantity * unitPrice;      // 18000
int discount = member ? 500 : 0;          // 0
int taxed = (subtotal - discount) * 110 / 100; // 19800
int billingAmount = taxed + shippingFee;  // 20600
```

期待出力例:
```text
小計: 18000
会員割引: 0
税込金額: 19800
請求金額: 20600
```

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、割引条件だけを変更する。
変更内容:
- 割引条件を `subtotal >= 5000 ? 1000 : 0` に変更

```java
int discount = subtotal >= 5000 ? 1000 : 0;
```

確認例（`quantity=10`, `unitPrice=1800`）:
- `subtotal = 18000`
- `discount = 1000`

期待出力例:
```text
小計: 18000
会員割引: 1000
税込金額: 18700
請求金額: 19500
```

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
