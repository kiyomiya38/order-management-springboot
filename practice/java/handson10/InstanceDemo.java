class Customer { // 顧客データを表すクラス
    String name; // 顧客名
    int point; // 保有ポイント
}

public class InstanceDemo { // 実行クラス
    public static void main(String[] args) {
        Customer c1 = new Customer(); // 1人目のインスタンス生成
        c1.name = "Tanaka"; // 1人目の名前
        c1.point = 120; // 1人目のポイント

        Customer c2 = new Customer(); // 2人目のインスタンス生成（c1とは別実体）
        c2.name = "Suzuki"; // 2人目の名前
        c2.point = 80; // 2人目のポイント

        System.out.println(c1.name + " point: " + c1.point); // c1 の状態を表示
        System.out.println(c2.name + " point: " + c2.point); // c2 の状態を表示
    } // main メソッドの終わり
} // クラス定義の終わり