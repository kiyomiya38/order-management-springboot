# Java-09 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-09-instances-and-classes.md`

## ミニ演習解答
```java
class Customer {
    String name;
    int point;

    void addPoint(int value) {
        point += value;
    }

    void usePoint(int value) {
        point -= value;
        if (point < 0) {
            point = 0; // 0未満は0へ補正
        }
    }
}

public class InstanceDemo {
    public static void main(String[] args) {
        Customer c1 = new Customer();
        c1.name = "Tanaka";
        c1.point = 120;

        Customer c2 = new Customer();
        c2.name = "Suzuki";
        c2.point = 80;

        Customer c3 = new Customer();
        c3.name = "Sato";
        c3.point = 50;

        // レベル1: 3件の状態を表示
        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);
        System.out.println(c3.name + " point: " + c3.point);

        // レベル2: 1件だけ加算
        c1.addPoint(30);
        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);

        // レベル3: 0未満にならないようにポイントを使用
        c1.usePoint(200);
        System.out.println(c1.name + " point: " + c1.point);
    }
}
```
