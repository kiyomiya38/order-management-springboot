class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
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

    static void notNull(String testName, Object value) {
        if (value == null) {
            throw new AssertionError(testName + " expected non-null but null");
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

public class Lesson4Main {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("重複usernameならsaveされない", () -> {
            FakeUserRepository fake = new FakeUserRepository();
            fake.existsResult = true;
            UserService service = new UserService(fake);

            MiniAssert.throwsMessage(
                    "duplicate username",
                    "その username は既に使われています。",
                    () -> service.createUser("yamada", "山田太郎"));
            MiniAssert.equals("saveCallCount", 0, fake.saveCallCount);
        });

        run("正常系なら1回saveされる", () -> {
            FakeUserRepository fake = new FakeUserRepository();
            fake.existsResult = false;
            UserService service = new UserService(fake);

            service.createUser("yamada", "山田太郎");
            MiniAssert.equals("saveCallCount", 1, fake.saveCallCount);
            MiniAssert.notNull("savedUser", fake.savedUser);
            MiniAssert.equals("username", "yamada", fake.savedUser.getUsername());
            MiniAssert.equals("displayName", "山田太郎", fake.savedUser.getDisplayName());
        });

        run("username必須チェック", () -> {
            FakeUserRepository fake = new FakeUserRepository();
            UserService service = new UserService(fake);
            MiniAssert.throwsMessage(
                    "username required",
                    "username は必須です。",
                    () -> service.createUser("", "山田太郎"));
            MiniAssert.equals("saveCallCount", 0, fake.saveCallCount);
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
