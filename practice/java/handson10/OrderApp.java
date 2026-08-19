public class OrderApp { // 実行クラス（エントリーポイント）
    public static void main(String[] args) {
        OrderItem item = new OrderItem(); // new で OrderItem のインスタンス（実体）を作成
        item.productName = "Laptop"; // 商品名を設定
        item.quantity = 2; // 数量を設定
        item.unitPrice = 120000; // 単価を設定

        OrderCalculator calculator = new OrderCalculator(); // 計算処理を担当するクラスのインスタンス
        int subtotal = calculator.calcSubtotal(item); // item（インスタンス参照）を引数として渡し、戻り値をローカル変数 subtotal で受け取る

        System.out.println(item.productName + " 小計: " + subtotal); // item.productName は「item の中のフィールド」、subtotal は main 内のローカル変数
    } // main メソッドの終わり
} // クラス定義の終わり