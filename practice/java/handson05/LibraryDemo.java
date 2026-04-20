import java.nio.file.Path; // パス情報を扱う型
import java.util.regex.Matcher; // 正規表現の検索結果を扱う型
import java.util.regex.Pattern; // 正規表現パターンを表す型

public class LibraryDemo { // Path と Pattern の基本利用例
    private static final Path STATIC_DIR = Path.of("static"); // クラス共通で使うディレクトリ定数
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\""); // "name" の値を抽出する正規表現

    public static void main(String[] args) {
        String body = "{\"name\":\"Tanaka\"}"; // 擬似的なJSON文字列
        Matcher matcher = NAME_PATTERN.matcher(body); // body に対して正規表現マッチャーを作成
        String name = ""; // 抽出結果を入れる変数（初期値は空文字）
        if (matcher.find()) { // パターンに一致する箇所があるか確認
            name = matcher.group(1); // 1番目のキャプチャグループ（name値）を取得
        }

        System.out.println("static dir: " + STATIC_DIR); // Path の値を表示
        System.out.println("name: " + name); // 抽出した name を表示
    } // main メソッドの終わり
} // クラス定義の終わり