public class StatusCheck {
    public static void main(String[] args) {
        int workedHours = 6;

        if (workedHours >= 8) {
            System.out.println("本日は勤務完了");
        } else {
            System.out.println("勤務中です");
        }
    }
}