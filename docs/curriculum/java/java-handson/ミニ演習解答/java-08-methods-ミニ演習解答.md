# Java-08 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-08-methods.md`

## レベル1（基本）解答
```java
static int calcBillingAmount(int quantity, int unitPrice, int taxRatePercent) {
    int subtotal = quantity * unitPrice;
    return subtotal * (100 + taxRatePercent) / 100;
}
```

---

## レベル2（拡張）解答
```java
static int calcBillingAmount(int quantity, int unitPrice, int taxRatePercent) {
    if (quantity <= 0) {
        return 0;
    }
    int subtotal = quantity * unitPrice;
    return subtotal * (100 + taxRatePercent) / 100;
}
```

---

## レベル3（実務）解答
```java
static void printStartMessage(String jobName) {
    System.out.println("[START] " + jobName);
}

public static void main(String[] args) {
    printStartMessage("受注取込");
    printStartMessage("在庫連携");
}
```

---

## 実行前予想問題の解答
上記ガード実装前提:
1. `calcBillingAmount(2, 1000, 10)` -> `2200`  
2. `calcBillingAmount(0, 1000, 10)` -> `0`

---

## デバッグ演習（任意）の解答
`int` 戻り値メソッドで `return "0";` は型不一致エラー。  
`return 0;` に修正して再コンパイルする。
