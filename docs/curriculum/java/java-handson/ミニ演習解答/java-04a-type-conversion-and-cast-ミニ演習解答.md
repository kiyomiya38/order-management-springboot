# Java-04A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-04a-type-conversion-and-cast.md`

## レベル1（基本）解答
変更内容:
- `orderCount` を `double` に変更

```java
double orderCount = 7;
System.out.println(orderCount);
```

期待出力例:
```text
7.0
```

---

## レベル2（拡張）解答
変更内容:
- `"1080"` を `parseInt` して税率10%を適用

```java
int basePrice = Integer.parseInt("1080");
int taxed = (int) Math.round(basePrice * 1.10);
System.out.println("税込: " + taxed);
```

期待出力例:
```text
税込: 1188
```

---

## レベル3（実務）解答
変更内容:
- 不正値を `try-catch` で捕捉

```java
String rawPrice = "10A0";
try {
    int price = Integer.parseInt(rawPrice);
    System.out.println(price);
} catch (NumberFormatException e) {
    System.out.println("不正な数値です: " + rawPrice);
}
```

---

## 実行前予想問題の解答
1. `System.out.println((int) 12.9);`
- 出力: `12`

2. `System.out.println(5 + 2.5);`
- 出力: `7.5`

---

## デバッグ演習（任意）の解答
`Integer.parseInt("12A")` は `NumberFormatException` になる。  
修正は `"12"` に戻して再実行する。
