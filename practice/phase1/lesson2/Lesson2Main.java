class User {
    private String employeeCode; // 社員番号。クラス外から直接変更させない
    private String name; // 氏名。クラス内で管理するデータ
    private String department; // 部署。変更はメソッド経由にする

    User(String employeeCode, String name, String department) { // new時に1回だけ実行される
        this.employeeCode = employeeCode; // 左:thisのフィールド / 右:引数
        this.name = name; // 引数nameをフィールドnameへ代入
        this.department = department; // 引数departmentをフィールドdepartmentへ代入
    }

    String displayName() { // 表示用文字列を作って返す
        return "[" + employeeCode + "] " + name + " - " + department;
    }

    void changeDepartment(String newDepartment) { // 部署変更処理
        this.department = newDepartment; // 新しい部署をフィールドへ反映
    }
}

public class Lesson2Main {
    public static void main(String[] args) {
        User user = new User("U001", "Yamada", "Sales"); // 引数3つで初期化
        System.out.println(user.displayName()); // 初期状態を表示

        user.changeDepartment("General Affairs"); // 部署を変更
        System.out.println(user.displayName()); // 変更後を表示

        User user2 = new User("U002", "Suzuki", "HR");
        user2.changeDepartment("IT");
        System.out.println(user2.displayName());

    }
}