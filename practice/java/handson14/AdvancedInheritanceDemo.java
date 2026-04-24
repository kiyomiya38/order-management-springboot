abstract class PaymentService { // 抽象クラス: 共通処理と未実装メソッドを持つ
    void printStart() { // 共通処理（全決済で使う）
        System.out.println("決済開始");
    }

    abstract int calculateFee(int amount); // 抽象メソッド: 子クラスで実装必須
}

class CardPaymentService extends PaymentService { // 具体的な決済サービス実装
    @Override
    int calculateFee(int amount) { // 抽象メソッドを実装
        return amount / 100; // 金額の1%を手数料とする
    }
}

public class AdvancedInheritanceDemo { // 実行クラス
    public static void main(String[] args) {
        CardPaymentService service = new CardPaymentService(); // 具象クラスを生成
        service.printStart(); // 抽象クラスで定義した共通処理を呼ぶ
        System.out.println("手数料: " + service.calculateFee(5000)); // 子クラス実装の計算結果を表示
    } // main メソッドの終わり
} // クラス定義の終わり