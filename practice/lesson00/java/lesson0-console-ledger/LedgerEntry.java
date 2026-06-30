public class LedgerEntry {
    private final LedgerType type;
    private final String category;
    private final int amount;
    private final String memo;

    LedgerEntry(LedgerType type, String category, int amount, String memo) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
    }

    LedgerType getType() {
        return type;
    }

    String getCategory() {
        return category;
    }

    int getAmount() {
        return amount;
    }

    String getMemo() {
        return memo;
    }
}