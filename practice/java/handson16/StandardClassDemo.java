class Product { // 商品クラス
    String code; // 商品コード

    Product(String code) { // コンストラクタ
        this.code = code; // フィールド初期化
    }
}

public class StandardClassDemo { // 実行クラス
    public static void main(String[] args) {
        Product p1 = new Product("P-001");

        System.out.println(p1); // Product をそのまま表示
        System.out.println("商品: " + p1); // 文字列連結の中で Product を使う
    } // main メソッドの終わり
} // クラス定義の終わり