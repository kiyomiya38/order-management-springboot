# Java-05 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-05-class-libraries.md`

## レベル1（基本）解答
```java
LocalDate after3Days = LocalDate.now().plusDays(3);
String id1 = UUID.randomUUID().toString();
String id2 = UUID.randomUUID().toString();

System.out.println("3日後: " + after3Days);
System.out.println("UUID1: " + id1);
System.out.println("UUID2: " + id2);
```

---

## レベル2（拡張）解答
```java
String raw = "  ABC  ";
System.out.println("trim前長さ: " + raw.length());
System.out.println("trim後長さ: " + raw.trim().length());

int price = 1980;
double taxRate = 0.08;
int taxed = (int) Math.round(price * (1 + taxRate));
System.out.println("税込価格: " + taxed); // 2138
```

---

## レベル3（実務）解答
```java
String businessOrderId = "ORD-" + orderId;
System.out.println("営業日=" + today + ", 注文ID=" + businessOrderId);
```

---

## 実行前予想問題の解答
1. `"  ABC  ".trim().length()` -> `3`  
2. `"ABC".length()` -> `3`

---

## デバッグ演習（任意）の解答
`import java.time.LocalDate;` を外すと `cannot find symbol`。  
`import` を戻して再コンパイルすれば解消する。
