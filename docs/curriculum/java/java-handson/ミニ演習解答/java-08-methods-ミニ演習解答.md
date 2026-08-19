# Java-08 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-08-methods.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 5の4引数版は残し、その後ろ（`main(...)`より前）へ5引数版を追加する。

```java
static int calcBillingAmount(
        int quantity,
        int unitPrice,
        int shippingFee,
        int discount,
        int taxRatePercent) {
    // 4引数版で求めた税適用前の金額を受け取る
    int amountBeforeTax = calcBillingAmount(quantity, unitPrice, shippingFee, discount);

    // 税率を適用した請求金額を返す
    return amountBeforeTax * (100 + taxRatePercent) / 100;
}
```

Step 5の`main(...)`内にある既存の表示処理より後へ、5引数版の呼び出しと表示を追加する。

```java
// 税率10%と8%を指定して5引数版を呼び出す
int amountWith10PercentTax = calcBillingAmount(4, 1800, 800, 500, 10);
int amountWith8PercentTax = calcBillingAmount(4, 1800, 800, 500, 8);

System.out.println("税率10% 請求金額: " + amountWith10PercentTax);
System.out.println("税率8% 請求金額: " + amountWith8PercentTax);
```

期待出力:
```text
税率10% 請求金額: 8250
税率8% 請求金額: 8100
```

---

## レベル2（拡張）解答
レベル1の完成コードは残す。4引数版の先頭（`subtotal`を計算する処理より前）へ、数量を検証する処理を追加する。

```java
static int calcBillingAmount(int quantity, int unitPrice, int shippingFee, int discount) {
    // 数量が0以下の場合は、送料や割引を計算せず0を返す
    if (quantity <= 0) {
        return 0;
    }

    int subtotal = calcSubtotal(quantity, unitPrice);
    return subtotal + shippingFee - discount;
}
```

`main(...)`内のレベル1の表示処理より後へ、4引数版を使う次の確認コードを追加する。

```java
// 単価、送料、割引は同じ値を使い、数量だけを変更して確認する
System.out.println("quantity=0 -> " + calcBillingAmount(0, 1800, 800, 500));
System.out.println("quantity=-2 -> " + calcBillingAmount(-2, 1800, 800, 500));
```

期待出力:
```text
quantity=0 -> 0
quantity=-2 -> 0
```

---

## レベル3（実務）解答
レベル2の完成コードはすべて残す。引数なし`printStartMessage()`の後ろ（`calcSubtotal(...)`より前）へ、引数あり版を追加する。

```java
static void printStartMessage(String jobName) {
    // 呼び出し元から受け取ったジョブ名を表示する
    System.out.println(jobName + " を開始します");
}
```

`main(...)`内の既存の`printStartMessage();`より後へ、次の2行を追加する。

```java
printStartMessage("受注取込");
printStartMessage("在庫同期");
```

期待出力:
```text
受注取込 を開始します
在庫同期 を開始します
```

### レベル3完了時の全コード

```java
public class MethodDemo {
    static void printStartMessage() {
        System.out.println("受注処理を開始します");
    }

    // ===== レベル3で追加: 引数で受け取った処理名を表示する =====
    static void printStartMessage(String jobName) {
        System.out.println(jobName + " を開始します");
    }
    // ===== レベル3で追加ここまで =====

    static int calcSubtotal(int quantity, int unitPrice) {
        return quantity * unitPrice;
    }

    // ===== レベル2で変更: 0以下の数量を先に拒否する =====
    static int calcBillingAmount(int quantity, int unitPrice, int shippingFee, int discount) {
        if (quantity <= 0) {
            return 0;
        }

        int subtotal = calcSubtotal(quantity, unitPrice);
        return subtotal + shippingFee - discount;
    }
    // ===== レベル2で変更ここまで =====

    // ===== レベル1で追加: 税率も引数で受け取る5引数版 =====
    static int calcBillingAmount(
            int quantity,
            int unitPrice,
            int shippingFee,
            int discount,
            int taxRatePercent) {
        int amountBeforeTax = calcBillingAmount(quantity, unitPrice, shippingFee, discount);
        return amountBeforeTax * (100 + taxRatePercent) / 100;
    }
    // ===== レベル1で追加ここまで =====

    public static void main(String[] args) {
        printStartMessage();

        // ===== レベル3で追加 =====
        printStartMessage("受注取込");
        printStartMessage("在庫同期");
        // ===== レベル3で追加ここまで =====

        int billingAmount = calcBillingAmount(4, 1800, 800, 500);
        System.out.println("請求金額: " + billingAmount);

        // ===== レベル1で追加 =====
        int amountWith10PercentTax = calcBillingAmount(4, 1800, 800, 500, 10);
        int amountWith8PercentTax = calcBillingAmount(4, 1800, 800, 500, 8);
        System.out.println("税率10% 請求金額: " + amountWith10PercentTax);
        System.out.println("税率8% 請求金額: " + amountWith8PercentTax);
        // ===== レベル1で追加ここまで =====

        // ===== レベル2で追加 =====
        System.out.println("quantity=0 -> " + calcBillingAmount(0, 1800, 800, 500));
        System.out.println("quantity=-2 -> " + calcBillingAmount(-2, 1800, 800, 500));
        // ===== レベル2で追加ここまで =====
    }
}
```

---

## 実行前予想問題の解答
Step 5の4引数版では、`quantity` が`0`の場合も送料と割引の計算が行われる。

1. `calcBillingAmount(2, 1000, 300, 100)` → `2200`
2. `calcBillingAmount(0, 1000, 300, 100)` → `200`

レベル2のガードを追加すると、2つ目の結果は`0`になる。

---

## デバッグ演習（任意）の解答
`calcSubtotal(...)` の戻り値型は `int` のため、文字列の `"0"` は返せない。

```text
incompatible types: String cannot be converted to int
```

`return 0;` に修正して再コンパイルする。
