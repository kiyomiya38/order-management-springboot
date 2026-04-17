# Java-17A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-17a-exception-types-and-throws.md`

## ミニ演習解答
1. `loadMode("")` の確認:

```java
try {
    System.out.println(loadMode(""));
} catch (ConfigException e) {
    System.out.println("ConfigException: " + e.getMessage());
}
```

2. `requirePositive(0)` の確認:

```java
try {
    System.out.println(requirePositive(0));
} catch (IllegalArgumentException e) {
    System.out.println("IllegalArgumentException: " + e.getMessage());
}
```

3. `main` に `throws ConfigException` を付ける構成:

```java
public static void main(String[] args) throws ConfigException {
    System.out.println(loadMode("")); // catchしない場合はここで例外伝播
}
```

補足:
- checked例外（`ConfigException`）は `catch` か `throws` が必須。
