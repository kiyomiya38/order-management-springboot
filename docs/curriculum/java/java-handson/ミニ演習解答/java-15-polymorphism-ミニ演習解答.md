# Java-15 ミニ演習解答

対象資料: `docs/curriculum/java/java-handson/java-15-polymorphism.md`
## 解答の読み方

- 本文のミニ演習をレベル1から順番に実施し、各レベル冒頭にある引き継ぎ・復元条件を優先します。
- 「全コード」と書かれたコードブロックは、そのファイル全体を置き換えます。
- 「追加」「変更」と書かれたコードブロックは、直前の説明で指定された位置だけへ反映し、ほかの既存コードは残します。
- コンパイルエラーを確認するための一時変更は、確認後に必ず元へ戻してから次のレベルへ進みます。

## レベル1（基本）解答
Step 6の`Intern`クラスより後、`PolymorphismDemo`クラスより前へ追加する。

```java
class Director extends Employee {
    @Override String roleLabel() { return "役員"; }
    @Override int monthlyBonus() { return 80000; }
}
```

`main(...)`で`Director`の実体を親型の`Employee`で受け、`Takahashi`を設定してStep 6の`Employee[]`へ追加する。以降のレベルでも、このクラスと配列要素は残す。

```java
Employee director = new Director();
director.name = "Takahashi";

Employee[] employees = { manager, partTimer, engineer, contractor, intern, director };
```

## レベル2（補足）解答
レベル1の変更を残したまま、`Engineer`にフィールドを追加する:

```java
String skillName;
```

Step 6の`engineer`変数は`Employee`型なので、実体の型を確認してから固有フィールドへ設定する:

```java
if (engineer instanceof Engineer) {
    Engineer engineerDetail = (Engineer) engineer;
    engineerDetail.skillName = "Java";
}
```

`employees`を処理する最初の`for`文では、各要素を`Employee`型で受け取る。`printEmployee(employee);`より後へ追加する:

```java
if (employee instanceof Engineer) {
    Engineer engineer = (Engineer) employee;
    System.out.println(engineer.name + " / " + engineer.skillName);
}
```

## レベル3（実務）解答
レベル1の`Director`とレベル2の`skillName`を残したまま、`Employee`に既定実装を追加する:

```java
String detailLabel() {
    return name + " は " + roleLabel();
}
```

`Engineer`でオーバーライドする:

```java
@Override
String detailLabel() {
    return name + " / " + skillName;
}
```

`printEmployee(Employee employee)`の1行目を、次の呼び出しへ変更する:

```java
System.out.println(employee.detailLabel());
```

レベル2で追加したスキル表示用の`instanceof`分岐は削除する。`printManagerTeam(...)`のように固有フィールドを扱う既存処理は、そのまま残す。

### レベル3完了時の全コード

```java
class Employee {
    String name;

    String roleLabel() {
        return "社員";
    }

    int monthlyBonus() {
        return 0;
    }

    // ===== レベル3で追加: 共通処理から呼べる詳細表示 =====
    String detailLabel() {
        return name + " は " + roleLabel();
    }
    // ===== レベル3で追加ここまで =====
}

class Manager extends Employee {
    String teamName;

    @Override
    String roleLabel() {
        return "管理者";
    }

    @Override
    int monthlyBonus() {
        return 50000;
    }
}

class PartTimer extends Employee {
    @Override
    String roleLabel() {
        return "アルバイト";
    }

    @Override
    int monthlyBonus() {
        return 0;
    }
}

class Engineer extends Employee {
    // ===== レベル2で追加: Engineerだけが持つ固有情報 =====
    String skillName;
    // ===== レベル2で追加ここまで =====

    @Override
    String roleLabel() {
        return "エンジニア";
    }

    @Override
    int monthlyBonus() {
        return 30000;
    }

    // ===== レベル3で追加: 固有情報を共通メソッドの実装へ閉じ込める =====
    @Override
    String detailLabel() {
        return name + " / " + skillName;
    }
    // ===== レベル3で追加ここまで =====
}

class Contractor extends Employee {
    @Override
    String roleLabel() {
        return "業務委託";
    }

    @Override
    int monthlyBonus() {
        return 10000;
    }
}

class Intern extends Employee {
    @Override
    String roleLabel() {
        return "インターン";
    }

    @Override
    int monthlyBonus() {
        return 0;
    }
}

// ===== レベル1で追加: 新しい社員種別 =====
class Director extends Employee {
    @Override
    String roleLabel() {
        return "役員";
    }

    @Override
    int monthlyBonus() {
        return 80000;
    }
}
// ===== レベル1で追加ここまで =====

public class PolymorphismDemo {
    static void printEmployee(Employee employee) {
        // ===== レベル3で変更: instanceofなしで実体ごとの詳細を表示する =====
        System.out.println(employee.detailLabel());
        // ===== レベル3で変更ここまで =====
        System.out.println("手当: " + employee.monthlyBonus());
    }

    static void printManagerTeam(Employee employee) {
        if (employee instanceof Manager) {
            Manager manager = (Manager) employee;
            System.out.println(manager.name + " / " + manager.teamName);
        }
    }

    public static void main(String[] args) {
        Employee manager = new Manager();
        manager.name = "Yamada";
        if (manager instanceof Manager) {
            Manager managerDetail = (Manager) manager;
            managerDetail.teamName = "Platform";
        }

        Employee partTimer = new PartTimer();
        partTimer.name = "Kato";

        Employee engineer = new Engineer();
        engineer.name = "Tanaka";
        // ===== レベル2で追加: 親型からEngineer固有情報を設定する =====
        if (engineer instanceof Engineer) {
            Engineer engineerDetail = (Engineer) engineer;
            engineerDetail.skillName = "Java";
        }
        // ===== レベル2で追加ここまで =====

        Employee contractor = new Contractor();
        contractor.name = "Sato";

        Employee intern = new Intern();
        intern.name = "Suzuki";

        // ===== レベル1で追加 =====
        Employee director = new Director();
        director.name = "Takahashi";
        // ===== レベル1で追加ここまで =====

        Employee[] employees = {
                manager, partTimer, engineer, contractor, intern, director
        };

        for (Employee employee : employees) {
            printEmployee(employee);
        }

        System.out.println("管理チーム:");
        for (Employee employee : employees) {
            printManagerTeam(employee);
        }
    }
}
```
