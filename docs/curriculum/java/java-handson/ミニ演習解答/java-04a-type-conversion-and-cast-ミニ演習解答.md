# Java-04A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-04a-type-conversion-and-cast.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
変更内容:
- Step 4の `priceText` を `"2500"` に変更

その他のコードは変更せず、そのまま使用する。

```java
String priceText = "2500";
```

期待出力例:
```text
変換前の価格: 2500
税込金額(double): 2750.0
請求金額(int): 2750
請求金額(String): 2750
```

---

## レベル2（拡張）解答
変更内容:
- レベル1の `taxRate` を `0.125` に変更

その他のコードは変更せず、レベル1から引き継ぐ。

```java
double taxRate = 0.125;
```

期待出力例:
```text
変換前の価格: 2500
税込金額(double): 2812.5
請求金額(int): 2812
請求金額(String): 2812
```

`double` から `int` へのキャストでは、小数点以下の `.5` が切り捨てられる。

---

## レベル3（実務）解答
変更内容:
- レベル2のコードへ数量文字列を追加し、小計に税率を適用

### レベル3完了時の全コード

```java
public class TypeConversionDemo {
    public static void main(String[] args) {
        String priceText = "2500";
        int price = Integer.parseInt(priceText);

        // ===== レベル3で追加: 数量文字列を数値へ変換する =====
        String quantityText = "3";
        int quantity = Integer.parseInt(quantityText);

        int subtotal = price * quantity;
        // ===== レベル3で追加ここまで =====

        double taxRate = 0.125;

        // ===== レベル3で変更: 価格ではなく小計に税率を適用する =====
        double taxedPrice = subtotal * (1 + taxRate);
        int billingAmount = (int) taxedPrice;
        String billingText = String.valueOf(billingAmount);
        // ===== レベル3で変更ここまで =====

        // ===== レベル3で追加: 数量と小計を表示する =====
        System.out.println("数量: " + quantity);
        System.out.println("小計: " + subtotal);
        // ===== レベル3で追加ここまで =====

        // レベル2までの表示処理はそのまま使用する
        System.out.println("変換前の価格: " + priceText);
        System.out.println("税込金額(double): " + taxedPrice);
        System.out.println("請求金額(int): " + billingAmount);
        System.out.println("請求金額(String): " + billingText);
    }
}
```

期待出力例:
```text
数量: 3
小計: 7500
変換前の価格: 2500
税込金額(double): 8437.5
請求金額(int): 8437
請求金額(String): 8437
```

---

## 実行前予想問題の解答
1. `System.out.println((int) 12.9);`
- 出力: `12`

2. `System.out.println(5 + 2.5);`
- 出力: `7.5`

---

## デバッグ演習（任意）の解答
`String` 型の値を `int` 型の変数へ直接代入できないため、`int price = priceText;` はコンパイルエラーになる。

```text
incompatible types: String cannot be converted to int
```

文字列を整数へ変換する `Integer.parseInt(...)` を使用して修正する。

```java
int price = Integer.parseInt(priceText);
```
