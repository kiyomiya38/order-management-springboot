class AttendanceService {
    private String status = "未出勤";

    String getStatus() {
        return status;
    }

    void clockIn() {
        status = "出勤中";
    }

    // わざと単純実装にしている。
    // この時点では「未出勤で退勤できてしまう」問題がある。
    void clockOut() {
        status = "退勤済み";
    }
}

public class Lesson1Main {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        AttendanceService service = new AttendanceService();

        check("初期状態", "未出勤", service.getStatus());

        service.clockIn();
        check("出勤後", "出勤中", service.getStatus());

        service.clockOut();
        check("退勤後", "退勤済み", service.getStatus());

        // 仕様としては「未出勤のまま退勤は不可」にしたい。
        // しかし今はエラーにならず退勤済みになってしまう。
        AttendanceService wrongCase = new AttendanceService();
        wrongCase.clockOut();
        check("未出勤で退勤（本来は失敗させたい）", "未出勤", wrongCase.getStatus());

        System.out.println();
        System.out.println("==== 結果 ====");
        System.out.println("PASS: " + passed);
        System.out.println("FAIL: " + failed);
    }

    private static void check(String testName, String expected, String actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("[PASS] " + testName + " expected=" + expected + " actual=" + actual);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName + " expected=" + expected + " actual=" + actual);
        }
    }
}
