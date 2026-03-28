class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

class AttendanceService {
    private String status = "未出勤";

    String getStatus() {
        return status;
    }

    void clockIn() {
        if ("出勤中".equals(status)) {
            throw new BusinessException("すでに出勤中です。");
        }
        if ("退勤済み".equals(status)) {
            throw new BusinessException("退勤済みのため再出勤できません。");
        }
        status = "出勤中";
    }

    void clockOut() {
        if (!"出勤中".equals(status)) {
            throw new BusinessException("退勤するには先に出勤してください。");
        }
        status = "退勤済み";
    }
}

class MiniAssert {
    static void equals(String testName, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(testName + " expected=" + expected + " actual=" + actual);
        }
    }

    static void throwsMessage(String testName, String expectedMessage, Runnable action) {
        try {
            action.run();
            throw new AssertionError(testName + " expected exception but nothing thrown");
        } catch (BusinessException ex) {
            if (!expectedMessage.equals(ex.getMessage())) {
                throw new AssertionError(
                        testName + " expectedMessage=" + expectedMessage + " actualMessage=" + ex.getMessage());
            }
        }
    }
}

public class Lesson2Main {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("初期状態は未出勤", () -> {
            AttendanceService service = new AttendanceService();
            MiniAssert.equals("status", "未出勤", service.getStatus());
        });

        run("出勤すると出勤中になる", () -> {
            AttendanceService service = new AttendanceService();
            service.clockIn();
            MiniAssert.equals("status", "出勤中", service.getStatus());
        });

        run("未出勤で退勤すると例外", () -> {
            AttendanceService service = new AttendanceService();
            MiniAssert.throwsMessage(
                    "clockOut without clockIn",
                    "退勤するには先に出勤してください。",
                    service::clockOut);
        });

        run("出勤後に退勤すると退勤済み", () -> {
            AttendanceService service = new AttendanceService();
            service.clockIn();
            service.clockOut();
            MiniAssert.equals("status", "退勤済み", service.getStatus());
        });

        System.out.println();
        System.out.println("==== 結果 ====");
        System.out.println("PASS: " + passed);
        System.out.println("FAIL: " + failed);
    }

    private static void run(String testName, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + testName);
        } catch (Throwable ex) {
            failed++;
            System.out.println("[FAIL] " + testName);
            System.out.println("  -> " + ex.getMessage());
        }
    }
}
