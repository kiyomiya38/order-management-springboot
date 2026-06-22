# Java-11A ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-11a-constructor-chaining.md`

## ミニ演習解答
1. `quantity` の補正:

```java
class Product {
    String name;
    int price;
    int quantity;

    Product() {
        this("UNKNOWN", 0, 0);
    }

    Product(String name) {
        this(name, 0, 0);
    }

    Product(String name, int price) {
        this(name, price, 0);
    }

    Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = Math.max(quantity, 0);
    }
}
```

2. `User` に引数なしコンストラクタを追加:

```java
class User {
    String name;

    User() {
        this("guest");
    }

    User(String name) {
        this.name = name;
    }
}
```

3. `this(...)` の前に代入を書くとエラー:
- `Constructor call must be the first statement in a constructor`
