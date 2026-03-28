class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

class User {
    private final long id;
    private final String name;

    User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    long getId() {
        return id;
    }

    String getName() {
        return name;
    }
}

class AttendanceService {
    private final java.util.Map<Long, String> statusByUserId = new java.util.HashMap<>();

    void registerUser(User user) {
        statusByUserId.put(user.getId(), "未出勤");
    }

    void clockIn(long userId) {
        String status = getStatusOrThrow(userId);
        if ("出勤中".equals(status)) {
            throw new BusinessException("すでに出勤中です。");
        }
        if ("退勤済み".equals(status)) {
            throw new BusinessException("退勤済みのため再出勤できません。");
        }
        statusByUserId.put(userId, "出勤中");
    }

    void clockOut(long userId) {
        String status = getStatusOrThrow(userId);
        if (!"出勤中".equals(status)) {
            throw new BusinessException("退勤するには先に出勤してください。");
        }
        statusByUserId.put(userId, "退勤済み");
    }

    String getStatus(long userId) {
        return getStatusOrThrow(userId);
    }

    private String getStatusOrThrow(long userId) {
        String status = statusByUserId.get(userId);
        if (status == null) {
            throw new BusinessException("対象ユーザーが存在しません。");
        }
        return status;
    }
}

class MiniAssert {
    static void equals(String testName, Object expected, Object actual) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
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

public class Lesson3Main {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("未登録ユーザーの出勤は例外", () -> {
            AttendanceService service = new AttendanceService();
            MiniAssert.throwsMessage(
                    "clockIn unknown user",
                    "対象ユーザーが存在しません。",
                    () -> service.clockIn(100L));
        });

        run("未出勤で退勤は例外", () -> {
            AttendanceService service = new AttendanceService();
            service.registerUser(new User(1L, "Yamada"));
            MiniAssert.throwsMessage(
                    "clockOut before clockIn",
                    "退勤するには先に出勤してください。",
                    () -> service.clockOut(1L));
        });

        run("出勤->退勤は成功", () -> {
            AttendanceService service = new AttendanceService();
            service.registerUser(new User(1L, "Yamada"));
            service.clockIn(1L);
            service.clockOut(1L);
            MiniAssert.equals("status", "退勤済み", service.getStatus(1L));
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
