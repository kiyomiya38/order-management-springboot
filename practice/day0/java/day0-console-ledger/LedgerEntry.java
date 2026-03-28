public class LedgerEntry {
    String type;      // INCOME or EXPENSE
    String category;  // 例: 給料, 食費
    int amount;       // 金額
    String memo;      // メモ

    LedgerEntry(String type, String category, int amount, String memo) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
    }
}
