class PriceUtil { // 価格計算ユーティリティクラス
    static int calcTaxIncluded(int basePrice) { // static: インスタンス化せず呼べる計算メソッド
        return basePrice * 110 / 100; // 税込(10%)を整数計算で求める
    }
}

class Product { // 商品クラス
    String name; // 商品名
    int price; // 税抜価格

    Product(String name, int price) { // コンストラクタ
        this.name = name; // 名前初期化
        this.price = price; // 価格初期化
    }
}

public class ClassMechanismDemo { // 実行クラス
    public static void main(String[] args) {
        Product p = new Product("Keyboard", 5000); // 商品生成
        int taxed = PriceUtil.calcTaxIncluded(p.price); // static メソッドで税込計算
        System.out.println(p.name + " 税込: " + taxed); // 結果表示
    } // main メソッドの終わり
} // クラス定義の終わり