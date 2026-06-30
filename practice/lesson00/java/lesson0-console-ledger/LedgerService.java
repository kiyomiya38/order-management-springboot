import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerService {
    private final List<LedgerEntry> entries = new ArrayList<>();

    void addEntry(String typeText, String category, int amount, String memo) {
        LedgerType type;
        try {
            type = LedgerType.valueOf(typeText);
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] 種別は INCOME または EXPENSE を指定してください");
            return;
        }

        if (amount <= 0) {
            System.out.println("[ERROR] 金額は1以上を指定してください");
            return;
        }

        if (category == null || category.isBlank()) {
            System.out.println("[ERROR] カテゴリは必須です");
            return;
        }

        LedgerEntry entry = new LedgerEntry(type, category.trim(), amount, normalizeMemo(memo));
        entries.add(entry);
        System.out.println("[OK] 登録: " + type + " / " + category + " / " + amount);
    }

    void printAll() {
        System.out.println("=== 取引一覧 ===");
        if (entries.isEmpty()) {
            System.out.println("データがありません");
            return;
        }

        int index = 1;
        for (LedgerEntry entry : entries) {
            System.out.println(index + ". "
                    + entry.getType()
                    + " | "
                    + entry.getCategory()
                    + " | "
                    + entry.getAmount()
                    + "円 | "
                    + entry.getMemo());
            index++;
        }
    }

    void printSummary() {
        int incomeTotal = 0;
        int expenseTotal = 0;
        Map<String, Integer> categoryTotals = new HashMap<>();

        for (LedgerEntry entry : entries) {
            if (entry.getType() == LedgerType.INCOME) {
                incomeTotal += entry.getAmount();
            } else {
                expenseTotal += entry.getAmount();
            }

            String key = entry.getType() + ":" + entry.getCategory();
            int current = categoryTotals.getOrDefault(key, 0);
            categoryTotals.put(key, current + entry.getAmount());
        }

        int balance = incomeTotal - expenseTotal;

        System.out.println("=== 集計 ===");
        System.out.println("収入合計: " + incomeTotal + "円");
        System.out.println("支出合計: " + expenseTotal + "円");
        System.out.println("収支: " + balance + "円");

        System.out.println("=== カテゴリ別 ===");
        for (String key : categoryTotals.keySet()) {
            System.out.println(key + " -> " + categoryTotals.get(key) + "円");
        }
    }

    void printBudgetAdvice(int budgetLimit) {
        int expenseTotal = 0;
        for (LedgerEntry entry : entries) {
            if (entry.getType() == LedgerType.EXPENSE) {
                expenseTotal += entry.getAmount();
            }
        }

        System.out.println("=== 予算チェック ===");
        System.out.println("予算上限: " + budgetLimit + "円");
        System.out.println("現在の支出: " + expenseTotal + "円");

        if (expenseTotal > budgetLimit) {
            System.out.println("[WARN] 予算を超えています");
        } else if (expenseTotal == budgetLimit) {
            System.out.println("[INFO] 予算ちょうどです");
        } else {
            System.out.println("[INFO] 予算内です");
        }
    }

    private String normalizeMemo(String memo) {
        if (memo == null || memo.isBlank()) {
            return "(メモなし)";
        }
        return memo.trim();
    }
}