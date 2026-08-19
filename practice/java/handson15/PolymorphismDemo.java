class Employee { // 親クラス
    String name; // 社員名

    String roleLabel() { // 役割名
        return "社員";
    }

    int monthlyBonus() { // 月額手当
        return 0;
    }
}

class Manager extends Employee { // 管理者
    @Override
    String roleLabel() {
        return "管理者";
    }

    @Override
    int monthlyBonus() {
        return 50000;
    }
}

class PartTimer extends Employee { // アルバイト
    @Override
    String roleLabel() {
        return "アルバイト";
    }

    @Override
    int monthlyBonus() {
        return 0;
    }
}

class Engineer extends Employee { // エンジニア
    @Override
    String roleLabel() {
        return "エンジニア";
    }

    @Override
    int monthlyBonus() {
        return 30000;
    }
}

class Contractor extends Employee { // 業務委託
    @Override
    String roleLabel() {
        return "業務委託";
    }

    @Override
    int monthlyBonus() {
        return 10000;
    }
}

public class PolymorphismDemo { // 実行クラス
    public static void main(String[] args) {
        Manager manager = new Manager(); // 子クラス型で受ける通常の書き方
        manager.name = "Yamada";

        PartTimer partTimer = new PartTimer();
        partTimer.name = "Kato";

        Engineer engineer = new Engineer();
        engineer.name = "Tanaka";

        Contractor contractor = new Contractor();
        contractor.name = "Sato";

        System.out.println(manager.name + " は " + manager.roleLabel());
        System.out.println("手当: " + manager.monthlyBonus());
        System.out.println(partTimer.name + " は " + partTimer.roleLabel());
        System.out.println("手当: " + partTimer.monthlyBonus());
        System.out.println(engineer.name + " は " + engineer.roleLabel());
        System.out.println("手当: " + engineer.monthlyBonus());
        System.out.println(contractor.name + " は " + contractor.roleLabel());
        System.out.println("手当: " + contractor.monthlyBonus());
    } // main メソッドの終わり
} // クラス定義の終わり