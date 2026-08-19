public class ExceptionTypesDemo {
    // NumberFormatException（ナンバー・フォーマット・エクセプション）は、
    // RuntimeException系の例外なのでunchecked例外
    // 数値に変換できない文字列を数値へ変換しようとしたときに発生する
    static int parseQuantity(String raw) {
        // Integer.parseInt(...)は、String型の文字列をint型の整数へ変換するメソッド
        // rawが「10」なら整数の10を返すが、「abc」は整数に変換できないため、
        // NumberFormatExceptionが発生する
        // unchecked例外なので、メソッド宣言へのthrowsの記述は必須ではない
        return Integer.parseInt(raw);
    }

    public static void main(String[] args) {
        // unchecked例外のcatchは文法上の必須条件ではない
        // 今回は入力間違いを案内して正常に処理を終えるため、設計上の判断でcatchする
        try {
            // 数値に変換できない「abc」を実引数として渡し、意図的に例外を発生させる
            int q = parseQuantity("abc");

            // 直前で例外が発生すると処理がcatchへ移るため、この行は実行されない
            System.out.println("quantity=" + q);
        } catch (NumberFormatException e) { // 発生した例外を変数eで受け取る
            // e.getClass()で発生した例外のクラス情報を取得する
            // getSimpleName()でパッケージ名を除いた短いクラス名を取得する
            // 今回取得できる文字列は「NumberFormatException」
            System.out.println("unchecked例外を捕捉: " + e.getClass().getSimpleName());
        }
    }
}