import java.util.HashMap;
import java.util.Map;

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

class User {
    private final String username;
    private final String role;

    User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    String getUsername() {
        return username;
    }

    String getRole() {
        return role;
    }
}

class Session {
    User currentUser;
}

class UserRepository {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, String> passwords = new HashMap<>();

    UserRepository() {
        users.put("admin", new User("admin", "ADMIN"));
        users.put("user", new User("user", "USER"));
        passwords.put("admin", "adminpass");
        passwords.put("user", "userpass");
    }

    User findByUsername(String username) {
        return users.get(username);
    }

    boolean passwordMatches(String username, String rawPassword) {
        String expected = passwords.get(username);
        return expected != null && expected.equals(rawPassword);
    }

    void addUser(String username, String role, String password) {
        users.put(username, new User(username, role));
        passwords.put(username, password);
    }
}

class AuthService {
    private final UserRepository userRepository;

    AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    String login(Session session, String username, String password) {
        if (!userRepository.passwordMatches(username, password)) {
            return "ログイン失敗";
        }
        session.currentUser = userRepository.findByUsername(username);
        return "ログイン成功";
    }
}

class HomeController {
    String home(Session session) {
        if (session.currentUser == null) {
            return "redirect:/login";
        }
        return "home: " + session.currentUser.getUsername();
    }

    String adminUsers(Session session) {
        if (session.currentUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.currentUser.getRole())) {
            return "403 Forbidden";
        }
        return "admin/users page";
    }
}

class AdminUserController {
    private final UserRepository userRepository;

    AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    String createUser(Session session, String username, String role) {
        if (session.currentUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(session.currentUser.getRole())) {
            return "403 Forbidden";
        }
        if (username == null || username.isBlank()) {
            throw new BusinessException("username は必須です。");
        }
        userRepository.addUser(username, role, "temporary");
        return "user created: " + username;
    }
}

public class Lesson1Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);
        HomeController homeController = new HomeController();
        AdminUserController adminUserController = new AdminUserController(userRepository);
        Session session = new Session();

        System.out.println("=== ログイン前 ===");
        System.out.println(homeController.home(session));
        System.out.println(homeController.adminUsers(session));

        System.out.println("=== userでログイン ===");
        System.out.println(authService.login(session, "user", "userpass"));
        System.out.println(homeController.home(session));
        System.out.println(homeController.adminUsers(session));

        System.out.println("=== adminでログインし直し ===");
        System.out.println(authService.login(session, "admin", "adminpass"));
        System.out.println(homeController.adminUsers(session));
        System.out.println(adminUserController.createUser(session, "tanaka", "USER"));
    }
}
