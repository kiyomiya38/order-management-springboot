# Java-16A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-16a-regex-basics.md`

## ミニ演習解答
1. `user@example.com` の簡易チェック:

```java
String email = "user@example.com";
boolean ok = email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
System.out.println(ok); // true
```

2. `find()` で2桁数字を順に抽出:

```java
Pattern p = Pattern.compile("\\d{2}");
Matcher m = p.matcher("A12 B34 C56");
while (m.find()) {
    System.out.println(m.group()); // 12, 34, 56
}
```

3. `^` / `$` を外したときの差:
- あり: `matches("^\\d{2}$")` は文字列全体が2桁のときだけ `true`
- なし: `matches("\\d{2}")` は `"A12"` のような文字列で `false`（全体一致のため）
- 部分一致したい場合は `find()` を使う
