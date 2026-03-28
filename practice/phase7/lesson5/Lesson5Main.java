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

class User {
    private final String username;
    private final String displayName;

    User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    String getUsername() {
        return username;
    }

    String getDisplayName() {
        return displayName;
    }
}

interface UserRepository {
    boolean existsByUsername(String username);

    void save(User user);
}

class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void createUser(String username, String displayName) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("username は必須です。");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new BusinessException("displayName は必須です。");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("その username は既に使われています。");
        }
        userRepository.save(new User(username, displayName));
    }
}

class FakeUserRepository implements UserRepository {
    boolean existsResult;
    int saveCallCount;
    User savedUser;

    @Override
    public boolean existsByUsername(String username) {
        return existsResult;
    }

    @Override
    public void save(User user) {
        saveCallCount++;
        savedUser = user;
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

public class Lesson5Main {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("Attendance: 初期状態", () -> {
            AttendanceService service = new AttendanceService();
            MiniAssert.equals("status", "未出勤", service.getStatus());
        });

        run("Attendance: 出勤->退勤", () -> {
            AttendanceService service = new AttendanceService();
            service.clockIn();
            service.clockOut();
            MiniAssert.equals("status", "退勤済み", service.getStatus());
        });

        run("Attendance: 未出勤退勤は例外", () -> {
            AttendanceService service = new AttendanceService();
            MiniAssert.throwsMessage(
                    "clockOut before clockIn",
                    "退勤するには先に出勤してください。",
                    service::clockOut);
        });

        run("User: 正常登録はsave 1回", () -> {
            FakeUserRepository fake = new FakeUserRepository();
            UserService service = new UserService(fake);
            service.createUser("yamada", "山田太郎");
            MiniAssert.equals("saveCallCount", 1, fake.saveCallCount);
            MiniAssert.equals("savedUsername", "yamada", fake.savedUser.getUsername());
        });

        run("User: 重複登録は例外", () -> {
            FakeUserRepository fake = new FakeUserRepository();
            fake.existsResult = true;
            UserService service = new UserService(fake);
            MiniAssert.throwsMessage(
                    "duplicate username",
                    "その username は既に使われています。",
                    () -> service.createUser("yamada", "山田太郎"));
            MiniAssert.equals("saveCallCount", 0, fake.saveCallCount);
        });

        System.out.println();
        System.out.println("==== 結果 ====");
        System.out.println("PASS: " + passed);
        System.out.println("FAIL: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
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
