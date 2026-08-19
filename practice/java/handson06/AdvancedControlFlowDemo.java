public class AdvancedControlFlowDemo {
    public static void main(String[] args) {
        String status = "PAID";

        switch (status) {
            case "PAID":
                System.out.println("状態: 入金済み");
                break;
            case "PENDING":
                System.out.println("状態: 入金待ち");
                break;
            default:
                System.out.println("状態: 不明");
                break;
        }

        int countdown = 3;
        do {
            System.out.println("開始まで: " + countdown);
            countdown--;
        } while (countdown >= 1);

        inspection:
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (row == 2 && col == 2) {
                    System.out.println("不正データ検出: row=" + row + ", col=" + col);
                    break inspection;
                }
                System.out.println("確認済み: row=" + row + ", col=" + col);
            }
        }
    }
}