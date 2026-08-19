# Java-03 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-03-variables-and-types.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 3の完成コードから開始し、指定された2つの値だけを変更する。Step 3のその他の変数と表示処理は残す。

変更内容:
1. `orderCode` を`"ORD-2026-9999"`へ変更
2. `paid` を `true` に変更

```java
public class VariableTypeDemo {
    public static void main(String[] args) {
        String orderCode = "ORD-2026-9999";
        long orderId = 10000000001L;
        int quantity = 3;
        int unitPrice = 1200;
        int totalPrice = quantity * unitPrice;
        double taxRate = 0.10;
        double taxAmount = totalPrice * taxRate;
        boolean paid = true;

        System.out.println("注文番号: " + orderCode);
        System.out.println("注文ID: " + orderId);
        System.out.println("合計: " + totalPrice);
        System.out.println("税額: " + taxAmount);
        System.out.println("支払済み: " + paid);
    }
}
```

期待出力例:
```text
注文番号: ORD-2026-9999
注文ID: 10000000001
合計: 3600
税額: 360.0
支払済み: true
```

---

## レベル2（拡張）解答
レベル1の完成コードを引き継ぎ、Step 3から存在する`taxRate`の値だけを変更する。`taxAmount`の計算と既存の表示は残す。
変更内容:
- `taxRate` を `0.08` に変更

```java
double taxRate = 0.08;
```

期待出力例:
```text
注文番号: ORD-2026-9999
注文ID: 10000000001
合計: 3600
税額: 288.0
支払済み: true
```

補足:
- 元の `0.10` では税額 `360.0`
- `0.08` にすると税額 `288.0`

---

## レベル3（実務）解答
レベル2の完成コードを引き継ぎ、`totalPrice`の型と値だけを変更する。
変更内容:
1. `int totalPrice` を `long totalPrice` に変更
2. `3000000000L` を代入

```java
long totalPrice = 3000000000L;
```

期待出力例:
```text
注文番号: ORD-2026-9999
注文ID: 10000000001
合計: 3000000000
税額: 2.4E8
支払済み: true
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
