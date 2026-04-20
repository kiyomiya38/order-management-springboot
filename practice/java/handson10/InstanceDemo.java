class Customer { // 顧客クラス
    String name; // 顧客名
    int point; // ポイント

    void setProfile(String name, int point) { // 顧客情報を一括設定するメソッド
        this.name = name; // this.name はフィールド、name は引数
        this.point = point; // this.point はフィールド、point は引数
    }
}

public class InstanceDemo { // 実行クラス
    public static void main(String[] args) {
        Customer c1 = new Customer(); // 1人目を生成
        c1.setProfile("Tanaka", 120); // プロフィール設定

        Customer c2 = new Customer(); // 2人目を生成
        c2.setProfile("Suzuki", 80); // プロフィール設定

        System.out.println(c1.name + " point: " + c1.point); // c1 の状態を表示
        System.out.println(c2.name + " point: " + c2.point); // c2 の状態を表示
    } // main メソッドの終わり
} // クラス定義の終わり