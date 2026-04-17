# Java-19 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-19-stream-api.md`

## ミニ演習解答
1. `amounts` から最大値取得:

```java
List<Integer> amounts = List.of(1200, 3000, 800, 4500);
int max = amounts.stream().mapToInt(Integer::intValue).max().orElse(0);
System.out.println("max=" + max); // 4500
```

2. `PAID` だけを `List` 取得:

```java
List<String> statuses = List.of("PAID", "PENDING", "PAID", "CANCELLED");
List<String> paidOnly = statuses.stream()
        .filter("PAID"::equals)
        .toList();
System.out.println(paidOnly); // [PAID, PAID]
```

3. `"ORD-" + 番号` へ変換:

```java
List<Integer> numbers = List.of(1, 2, 3);
numbers.stream()
       .map(n -> "ORD-" + n)
       .forEach(System.out::println);
```
