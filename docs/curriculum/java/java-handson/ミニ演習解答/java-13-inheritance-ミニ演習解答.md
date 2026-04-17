# Java-13 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-13-inheritance.md`

## ミニ演習解答
```java
class Employee {
    String name;

    String roleLabel() {
        return "社員";
    }

    void printProfile() {
        System.out.println(roleLabel() + ": " + name);
    }
}

class Manager extends Employee {
    @Override
    String roleLabel() {
        return "管理者";
    }
}

class PartTimeEmployee extends Employee {
    @Override
    String roleLabel() {
        return "アルバイト";
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Employee e1 = new Manager();
        e1.name = "Tanaka";
        Employee e2 = new PartTimeEmployee();
        e2.name = "Sato";

        e1.printProfile(); // 管理者: Tanaka
        e2.printProfile(); // アルバイト: Sato
    }
}
```
