# Java-18 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-18-collections.md`

## ミニ演習解答
1. `List` に商品名5件:

```java
List<String> products = new ArrayList<>();
products.add("Laptop");
products.add("Mouse");
products.add("Keyboard");
products.add("Display");
products.add("Dock");
products.forEach(System.out::println);
```

2. `Set` の重複除去確認:

```java
Set<String> tags = new HashSet<>();
tags.add("PAID");
tags.add("PAID");
tags.add("URGENT");
System.out.println(tags.size()); // 2
```

3. `Map` 同一キー上書き確認:

```java
Map<String, Integer> stockByCode = new HashMap<>();
stockByCode.put("P-001", 12);
stockByCode.put("P-001", 20); // 上書き
System.out.println(stockByCode.get("P-001")); // 20
```
