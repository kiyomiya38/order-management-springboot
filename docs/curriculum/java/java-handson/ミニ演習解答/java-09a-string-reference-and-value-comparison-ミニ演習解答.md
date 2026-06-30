# Java-09A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-09a-string-reference-and-value-comparison.md`

## レベル1（基本）解答
```java
String original = new String("PAID");
String sameValue = new String("PENDING");

System.out.println("sameValue.equals(original): " + sameValue.equals(original));
```

期待出力例:
```text
sameValue.equals(original): false
```

---

## レベル2（拡張）解答
```java
String original = new String("PAID");
String anotherReference = original;

System.out.println("anotherReference == original: " + (anotherReference == original));
System.out.println("anotherReference.equals(original): " + anotherReference.equals(original));
```

期待出力例:
```text
anotherReference == original: true
anotherReference.equals(original): true
```

---

## レベル3（実務）解答
```java
String[] statuses = {"PAID", "PENDING", "CANCELLED", "PENDING"};
int pendingCount = 0;

for (String status : statuses) {
    if ("PENDING".equals(status)) {
        pendingCount++;
    }
}

System.out.println("PENDING件数: " + pendingCount);
```

期待出力例:
```text
PENDING件数: 2
```

---

## 実行前予想問題の解答
1. `a == b` は、別々のインスタンスを参照しているため `false`
2. `a.equals(b)` は、文字列の内容が同じため `true`

---

## デバッグ演習（任意）の解答
`s1 == s2` は参照先を比較するため `false`、`s1.equals(s2)` は内容を比較するため `true`。
