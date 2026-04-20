public class EncapsulationDemo { // UserAccount 利用側の実行クラス
    public static void main(String[] args) {
        UserAccount user = new UserAccount(); // インスタンス生成
        user.setUsername("tanaka"); // setter 経由で値を設定
        user.setAge(25); // setter 経由で値を設定

        System.out.println("username: " + user.getUsername()); // getter 経由で値を取得
        System.out.println("age: " + user.getAge()); // getter 経由で値を取得
    } // main メソッドの終わり
} // クラス定義の終わり