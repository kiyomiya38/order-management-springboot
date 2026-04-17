# Java-15 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-15-polymorphism.md`

## ミニ演習解答
```java
class Employee {
    String name;
    String roleLabel() { return "社員"; }
    String detail() { return roleLabel() + ": " + name; } // 共通メソッド
}

class Manager extends Employee {
    @Override String roleLabel() { return "管理者"; }
}

class PartTimer extends Employee {
    @Override String roleLabel() { return "アルバイト"; }
}

class Engineer extends Employee {
    @Override String roleLabel() { return "エンジニア"; }
}

public class PolymorphismDemo {
    static void printRole(Employee e) {
        System.out.println(e.detail());
    }

    public static void main(String[] args) {
        Employee[] employees = { new Manager(), new PartTimer(), new Engineer() };
        employees[0].name = "A";
        employees[1].name = "B";
        employees[2].name = "C";

        for (Employee e : employees) {
            printRole(e); // 共通メソッドだけで処理

            if (e instanceof Manager) {
                System.out.println("-> 管理機能あり");
            } else if (e instanceof PartTimer) {
                System.out.println("-> 時間帯勤務");
            } else if (e instanceof Engineer) {
                System.out.println("-> 技術担当");
            }
        }
    }
}
```
