# Java-17 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-17-exceptions.md`

## ミニ演習解答
```java
static int validateQuantity(int quantity) {
    if (quantity <= 0 || quantity > 1000) {
        throw new IllegalArgumentException("quantity が不正です: " + quantity);
    }
    return quantity;
}

static int validatePrice(int price) {
    if (price < 0) {
        throw new IllegalArgumentException("price が不正です: " + price);
    }
    return price;
}

public static void main(String[] args) {
    try {
        int q = validateQuantity(1200);
        int p = validatePrice(-1);
        System.out.println("q=" + q + ", p=" + p);
    } catch (IllegalArgumentException e) {
        System.out.println("入力エラー: " + e.getMessage());
    }
}
```
