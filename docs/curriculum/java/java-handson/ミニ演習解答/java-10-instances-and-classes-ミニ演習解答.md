# Java-10 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-10-instances-and-classes.md`

## ミニ演習解答
```java
class Customer {
    String name;
    int point;

    void setProfile(String name, int point) {
        this.name = name;
        this.point = Math.max(point, 0); // 0未満は0へ補正
    }

    void addPoint(int value) {
        this.point += value;
    }
}

public class InstanceDemo {
    public static void main(String[] args) {
        Customer c1 = new Customer();
        c1.setProfile("Tanaka", 120);

        Customer c2 = new Customer();
        c2.setProfile("Suzuki", 80);
        c2.addPoint(20); // 1件だけ加算

        Customer c3 = new Customer();
        c3.setProfile("Sato", -5); // 補正で0

        System.out.println(c1.name + " point: " + c1.point);
        System.out.println(c2.name + " point: " + c2.point);
        System.out.println(c3.name + " point: " + c3.point);
    }
}
```
