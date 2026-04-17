# Java-11 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-11-class-mechanisms.md`

## ミニ演習解答
```java
class Product {
    static int createdCount = 0;
    String name;
    int price;
    int quantity;

    Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        createdCount++;
    }
}

class PriceUtil {
    static int calcDiscounted(int basePrice, int discountRatePercent) {
        return basePrice * (100 - discountRatePercent) / 100;
    }
}

public class ClassMechanismDemo {
    public static void main(String[] args) {
        Product p1 = new Product("Laptop", 120000, 2);
        Product p2 = new Product("Mouse", 2500, 5);

        int discounted = PriceUtil.calcDiscounted(p1.price, 10);
        System.out.println("割引後価格: " + discounted);
        System.out.println("生成数: " + Product.createdCount);
        System.out.println(p1.name + " 在庫: " + p1.quantity);
        System.out.println(p2.name + " 在庫: " + p2.quantity);
    }
}
```
