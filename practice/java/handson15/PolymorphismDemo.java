class Employee { // 社員データ
    String name; // 社員名
    String employeeType; // 種類を文字列で持つ
}

public class PolymorphismDemo { // 実行クラス
    static String roleLabel(String employeeType) { // 種類ごとに役割名を返す
        if (employeeType.equals("manager")) {
            return "管理者";
        } else if (employeeType.equals("partTimer")) {
            return "アルバイト";
        } else if (employeeType.equals("engineer")) {
            return "エンジニア";
        } else if (employeeType.equals("contractor")) { // 追加1: 新しい種類の役割名
            return "業務委託";
        }
        return "社員";
    }

    static int monthlyBonus(String employeeType) { // 種類ごとに月額手当を返す
        if (employeeType.equals("manager")) {
            return 50000;
        } else if (employeeType.equals("partTimer")) {
            return 0;
        } else if (employeeType.equals("engineer")) {
            return 30000;
        } else if (employeeType.equals("contractor")) { // 追加2: 新しい種類の手当
            return 10000;
        }
        return 0;
    }

    static void printEmployee(Employee employee) { // 表示処理
        System.out.println(employee.name + " は " + roleLabel(employee.employeeType));
        System.out.println("手当: " + monthlyBonus(employee.employeeType));
    }

    public static void main(String[] args) {
        Employee manager = new Employee();
        manager.name = "Yamada";
        manager.employeeType = "manager";

        Employee partTimer = new Employee();
        partTimer.name = "Kato";
        partTimer.employeeType = "partTimer";

        Employee engineer = new Employee();
        engineer.name = "Tanaka";
        engineer.employeeType = "engineer";

        Employee contractor = new Employee(); // 追加3: 新しい種類のデータ
        contractor.name = "Sato";
        contractor.employeeType = "contractor";

        Employee[] employees = { manager, partTimer, engineer, contractor };

        for (Employee employee : employees) {
            printEmployee(employee);
        }
    } // main メソッドの終わり
} // クラス定義の終わり