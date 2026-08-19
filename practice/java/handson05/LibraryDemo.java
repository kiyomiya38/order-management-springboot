import java.time.LocalDate; // 日付のみを扱うクラス
import java.time.LocalDateTime; // 日時を扱うクラス
import java.util.UUID; // 一意な識別子を生成するクラス

public class LibraryDemo { // 代表的な標準ライブラリの利用例
    public static void main(String[] args) {
        String rawName = "  Shinesoft  "; // 先頭と末尾に空白を含む文字列
        String normalized = rawName.trim(); // trim() で前後空白を除去
        int rawLength = rawName.length();
        int normalizedLength = normalized.length();

        int price = 1980; // 税抜価格
        double taxRate = 0.08; // 税率 10%
        int taxed = (int) Math.round(price * (1 + taxRate)); // Math.round(...) は小数を四捨五入して long を返す（.5 以上切り上げ）ため、(int)
                                                             // で型を合わせている
        int max = Math.max(900, taxed); // 900 と taxed の大きい方を取得

        LocalDate today = LocalDate.now(); // 今日の日付を取得
        LocalDateTime now = LocalDateTime.now(); // 現在の日時を取得
        String orderId = UUID.randomUUID().toString(); // ランダムなUUIDを文字列化

        LocalDate threeDaysLater = today.plusDays(3);
        String secondOrderId = UUID.randomUUID().toString();
        String businessOrderId = "ORD-" + orderId;

        System.out.println("元の文字列: [" + rawName + "]"); // 加工前を表示
        System.out.println("整形後: [" + normalized + "]"); // 加工後を表示
        System.out.println("空白だけか: " + "   ".isBlank()); // isBlank() で空白だけか判定
        System.out.println("税込価格(四捨五入): " + taxed); // 計算結果を表示
        System.out.println("比較結果(大きい方): " + max); // 比較結果を表示
        System.out.println("営業日: " + today); // 日付を表示
        System.out.println("処理時刻: " + now); // 日時を表示
        System.out.println("注文ID: " + orderId); // 生成したIDを表示
        System.out.println("trim前 length: " + rawLength);
        System.out.println("trim後 length: " + normalizedLength);

        System.out.println("3日後: " + threeDaysLater);
        System.out.println("UUID-1: " + orderId);
        System.out.println("UUID-2: " + secondOrderId);

        System.out.println(today + " / " + businessOrderId);
    } // main メソッドの終わり
} // クラス定義の終わり