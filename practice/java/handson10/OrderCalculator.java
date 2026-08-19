public class OrderCalculator { // 注文金額を計算するクラス
    int calcSubtotal(OrderItem item) { // 引数: OrderItem型の変数 item（注文データ1件分の参照）を受け取る
        return item.quantity * item.unitPrice; // itemの中の quantity と unitPrice を使って計算し、結果(int)を呼び出し元へ返す
    }
} // クラス定義の終わり