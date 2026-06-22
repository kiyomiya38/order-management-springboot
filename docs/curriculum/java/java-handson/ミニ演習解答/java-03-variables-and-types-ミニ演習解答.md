# Java-03 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-03-variables-and-types.md`

## レベル1（基本）解答
変更内容:
1. `orderCode` を別値へ変更
2. `paid` を `true` に変更

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        String orderCode = "ORD-2026-0099";
        int quantity = 3;
        int unitPrice = 1200;
        int totalPrice = quantity * unitPrice;
        boolean paid = true;

        System.out.println("注文番号: " + orderCode);
        System.out.println("数量: " + quantity);
        System.out.println("単価: " + unitPrice);
        System.out.println("合計: " + totalPrice);
        System.out.println("支払済み: " + paid);
    }
}
```

期待出力例:
```text
注文番号: ORD-2026-0099
数量: 3
単価: 1200
合計: 3600
支払済み: true
```

---

## レベル2（拡張）解答
変更内容:
- `taxRate` を `0.08` に変更

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        int totalPrice = 3600;
        double taxRate = 0.08;
        double taxAmount = totalPrice * taxRate;

        System.out.println("合計: " + totalPrice);
        System.out.println("税率: " + taxRate);
        System.out.println("税額: " + taxAmount);
    }
}
```

期待出力例:
```text
合計: 3600
税率: 0.08
税額: 288.0
```

補足:
- 元の `0.10` では税額 `360.0`
- `0.08` にすると税額 `288.0`

---

## レベル3（実務）解答
変更内容:
1. `int totalPrice` を `long totalPrice` に変更
2. `3000000000L` を代入

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        long totalPrice = 3000000000L;
        System.out.println("合計: " + totalPrice);
    }
}
```

期待出力例:
```text
合計: 3000000000
```

補足:
- `int totalPrice = 3000000000;` は上限超過でコンパイルエラーになる。
- `long` と `L` をセットで使うと扱える。

---

## 実行前予想問題の解答
1. `int a = 10; double b = 1.5; System.out.println(a + b);`
- 出力: `11.5`

2. `String code = "100"; System.out.println(code + 20);`
- 出力: `10020`

---

## デバッグ演習（任意）の解答
問題コード:
```java
int quantity = "3";
```

発生エラー例:
- `incompatible types: String cannot be converted to int`

修正例1（整数として扱う）:
```java
int quantity = 3;
```

修正例2（文字列として扱う）:
```java
String quantity = "3";
```

補足:
- 「変数の型」と「右辺の値の型」を一致させるのが基本。
