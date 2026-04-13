public class StringRuleDemo { // 文字列の判定と整形ルールを確認するクラス
    static String normalize(String name) { // 入力nameを「表示用の安全な文字列」に整える
        if (name == null || name.isBlank()) { // nullまたは空白だけならguest扱いにする
            return "guest"; // 代替文字列を返す
        } // ifブロックの終わり
        return name.trim(); // それ以外は前後の空白を削って返す
    } // normalizeメソッドの終わり

    public static void main(String[] args) { // 実行入口
        System.out.println(normalize(null)); // null入力の結果を確認する
        System.out.println(normalize("   ")); // 空白だけ入力の結果を確認する
        System.out.println(normalize("  Alice  ")); // 前後空白つき入力の結果を確認する
    } // mainメソッドの終わり
} // StringRuleDemoクラスの終わり