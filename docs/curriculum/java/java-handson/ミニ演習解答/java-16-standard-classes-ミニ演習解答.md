# Java-16 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-16-standard-classes.md`

## ミニ演習解答
1. 不正文字列の `parseInt`:

```java
try {
    Integer.parseInt("12A");
} catch (NumberFormatException e) {
    System.out.println("変換失敗: " + e.getMessage());
}
```

2. `StringBuilder` で3行ログ:

```java
StringBuilder sb = new StringBuilder();
sb.append("INFO start").append(System.lineSeparator());
sb.append("INFO validate").append(System.lineSeparator());
sb.append("INFO end");
System.out.println(sb);
```

3. `hashCode` 実装と `HashSet` 重複判定:

```java
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class Product {
    String code;
    Product(String code) { this.code = code; }
    @Override public boolean equals(Object o) {
        return o instanceof Product p && Objects.equals(code, p.code);
    }
    @Override public int hashCode() {
        return Objects.hash(code);
    }
}

Set<Product> set = new HashSet<>();
set.add(new Product("P-001"));
set.add(new Product("P-001"));
System.out.println(set.size()); // 1
```

4. `body` を変更した抽出確認:

```java
String body = "{\"name\":\"Suzuki\"}";
Matcher matcher = NAME_PATTERN.matcher(body);
if (matcher.find()) {
    System.out.println("name: " + matcher.group(1)); // Suzuki
}
```

5. `final` の挙動確認:
確認手順:
1. `private static final Path STATIC_DIR = Path.of("static");` の `final` を外す
2. `STATIC_DIR = Path.of("static2");` を代入してコンパイル
3. 再確認後は `final` を戻す
