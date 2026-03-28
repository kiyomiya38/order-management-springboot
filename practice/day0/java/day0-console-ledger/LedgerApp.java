public class LedgerApp {
    public static void main(String[] args) {
        LedgerService service = new LedgerService();

        System.out.println("=== 家計簿Lite (Console) ===");

        // サンプルデータ登録
        service.addEntry("INCOME", "給料", 280000, "3月分");
        service.addEntry("EXPENSE", "食費", 18000, "スーパー");
        service.addEntry("EXPENSE", "交通費", 9000, "定期券");
        service.addEntry("EXPENSE", "娯楽", 12000, "映画・書籍");
        service.addEntry("INCOME", "副業", 30000, "Web制作");

        // バリデーション動作確認（意図的にエラー）
        service.addEntry("PAY", "不正種別", 1000, "テスト");
        service.addEntry("EXPENSE", "", 2000, "カテゴリ空");
        service.addEntry("EXPENSE", "食費", -500, "負の金額");

        // 一覧表示 / 集計表示 / 予算チェック
        service.printAll();
        service.printSummary();
        service.printBudgetAdvice(45000);
    }
}
