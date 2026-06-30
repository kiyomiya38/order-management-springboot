class Employee { // 親クラス
    String name; // 社員名

    String roleLabel() { // 役割名を返すメソッド（親の既定実装）
        return "社員";
    }

    void printProfile() { // プロフィール表示メソッド
        System.out.println(roleLabel() + ": " + name); // roleLabel は実体に応じた実装が呼ばれる
    }
}

class Manager extends Employee { // 子クラス
    @Override
    String roleLabel() { // 親メソッドを上書き（オーバーライド）
        return "管理者";
    }
}

public class InheritanceDemo { // 実行クラス
    public static void main(String[] args) {
        Manager m = new Manager(); // Manager を生成
        m.name = "Tanaka"; // 名前設定
        m.printProfile(); // オーバーライド結果を含めて表示
    } // main メソッドの終わり
} // クラス定義の終わり