import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class User {
    private final String username;
    private final String displayName;
    private final String role;

    User(String username, String displayName, String role) {
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }

    String getUsername() {
        return username;
    }

    String getDisplayName() {
        return displayName;
    }

    String getRole() {
        return role;
    }
}

class Session {
    User currentUser;
}

class UserRepository {
    private final Map<String, User> users = new LinkedHashMap<>();
    private final Map<String, String> passwords = new HashMap<>();

    UserRepository() {
        saveWithPassword(new User("admin", "管理者", "ADMIN"), "adminpass");
        saveWithPassword(new User("user", "一般ユーザー", "USER"), "userpass");
    }

    User findByUsername(String username) {
        return users.get(username);
    }

    boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    boolean passwordMatches(String username, String rawPassword) {
        String expected = passwords.get(username);
        return expected != null && expected.equals(rawPassword);
    }

    void save(User user) {
        users.put(user.getUsername(), user);
    }

    void saveWithPassword(User user, String password) {
        save(user);
        passwords.put(user.getUsername(), password);
    }

    Map<String, User> findAll() {
        return users;
    }
}

class AuthService {
    private final UserRepository userRepository;

    AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    String login(Session session, String username, String password) {
        if (!userRepository.passwordMatches(username, password)) {
            return "login failed";
        }
        session.currentUser = userRepository.findByUsername(username);
        return "redirect:/home";
    }

    String logout(Session session) {
        session.currentUser = null;
        return "redirect:/login";
    }
}

class AuthGuard {
    User requireLogin(Session session) {
        if (session.currentUser == null) {
            throw new IllegalStateException("redirect:/login");
        }
        return session.currentUser;
    }

    User requireAdmin(Session session) {
        User user = requireLogin(session);
        if (!"ADMIN".equals(user.getRole())) {
            throw new IllegalStateException("403 Forbidden");
        }
        return user;
    }
}

class UserCreateForm {
    String username;
    String displayName;
    String role;
}

class ValidationResult {
    private final Map<String, String> errors = new LinkedHashMap<>();

    void addError(String field, String message) {
        if (!errors.containsKey(field)) {
            errors.put(field, message);
        }
    }

    boolean hasErrors() {
        return !errors.isEmpty();
    }

    Map<String, String> getErrors() {
        return errors;
    }
}

class UserCreateValidator {
    private final UserRepository userRepository;

    UserCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    ValidationResult validate(UserCreateForm form) {
        ValidationResult result = new ValidationResult();
        if (form.username == null || form.username.isBlank()) {
            result.addError("username", "username は必須です。");
        } else if (userRepository.existsByUsername(form.username)) {
            result.addError("username", "その username は既に使われています。");
        }
        if (form.displayName == null || form.displayName.isBlank()) {
            result.addError("displayName", "displayName は必須です。");
        }
        if (!"USER".equals(form.role) && !"ADMIN".equals(form.role)) {
            result.addError("role", "role は USER か ADMIN を指定してください。");
        }
        return result;
    }
}

class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void createUser(UserCreateForm form) {
        userRepository.saveWithPassword(new User(form.username, form.displayName, form.role), "temporary");
    }

    String listUsers() {
        StringBuilder sb = new StringBuilder("users=");
        for (User user : userRepository.findAll().values()) {
            sb.append(user.getUsername()).append("(").append(user.getRole()).append(") ");
        }
        return sb.toString().trim();
    }
}

class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    String login(Session session, Map<String, String> form) {
        return authService.login(session, form.getOrDefault("username", ""), form.getOrDefault("password", ""));
    }

    String logout(Session session) {
        return authService.logout(session);
    }
}

class HomeController {
    private final AuthGuard authGuard;
    private final UserService userService;

    HomeController(AuthGuard authGuard, UserService userService) {
        this.authGuard = authGuard;
        this.userService = userService;
    }

    String home(Session session) {
        try {
            User me = authGuard.requireLogin(session);
            return "home: " + me.getUsername() + " / " + me.getDisplayName();
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }
    }

    String adminUsers(Session session) {
        try {
            authGuard.requireAdmin(session);
            return userService.listUsers();
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }
    }
}

class AdminUserController {
    private final AuthGuard authGuard;
    private final UserCreateValidator validator;
    private final UserService userService;

    AdminUserController(AuthGuard authGuard, UserCreateValidator validator, UserService userService) {
        this.authGuard = authGuard;
        this.validator = validator;
        this.userService = userService;
    }

    String create(Session session, Map<String, String> formMap) {
        try {
            authGuard.requireAdmin(session);
            UserCreateForm form = new UserCreateForm();
            form.username = formMap.getOrDefault("username", "");
            form.displayName = formMap.getOrDefault("displayName", "");
            form.role = formMap.getOrDefault("role", "USER");

            ValidationResult validationResult = validator.validate(form);
            if (validationResult.hasErrors()) {
                return "400 " + validationResult.getErrors();
            }
            userService.createUser(form);
            return "redirect:/admin/users";
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }
    }
}

class Request {
    private final String method;
    private final String path;
    private final Session session;
    private final Map<String, String> form;

    Request(String method, String path, Session session, Map<String, String> form) {
        this.method = method;
        this.path = path;
        this.session = session;
        this.form = form;
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    Session getSession() {
        return session;
    }

    Map<String, String> getForm() {
        return form;
    }
}

interface RouteHandler {
    String handle(Request request);
}

class Router {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    void add(String method, String path, RouteHandler handler) {
        routes.put(method + " " + path, handler);
    }

    String dispatch(Request request) {
        RouteHandler handler = routes.get(request.getMethod() + " " + request.getPath());
        if (handler == null) {
            return "404 Not Found";
        }
        return handler.handle(request);
    }
}

class AppConfig {
    private final Router router;

    AppConfig() {
        UserRepository userRepository = new UserRepository();
        AuthService authService = new AuthService(userRepository);
        AuthGuard authGuard = new AuthGuard();
        UserCreateValidator userCreateValidator = new UserCreateValidator(userRepository);
        UserService userService = new UserService(userRepository);

        AuthController authController = new AuthController(authService);
        HomeController homeController = new HomeController(authGuard, userService);
        AdminUserController adminUserController =
                new AdminUserController(authGuard, userCreateValidator, userService);

        this.router = new Router();
        router.add("GET", "/home", req -> homeController.home(req.getSession()));
        router.add("GET", "/admin/users", req -> homeController.adminUsers(req.getSession()));
        router.add("POST", "/admin/users/new", req -> adminUserController.create(req.getSession(), req.getForm()));
        router.add("POST", "/login", req -> authController.login(req.getSession(), req.getForm()));
        router.add("POST", "/logout", req -> authController.logout(req.getSession()));
        router.add("GET", "/health", req -> "OK");
    }

    Router router() {
        return router;
    }
}

public class Lesson4Main {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        Router router = appConfig.router();
        Session session = new Session();

        System.out.println(router.dispatch(new Request("GET", "/home", session, Map.of())));

        System.out.println(
                router.dispatch(
                        new Request(
                                "POST",
                                "/login",
                                session,
                                Map.of("username", "admin", "password", "adminpass"))));

        System.out.println(router.dispatch(new Request("GET", "/home", session, Map.of())));
        System.out.println(router.dispatch(new Request("GET", "/admin/users", session, Map.of())));

        System.out.println(
                router.dispatch(
                        new Request(
                                "POST",
                                "/admin/users/new",
                                session,
                                Map.of("username", "tanaka", "displayName", "田中", "role", "USER"))));

        System.out.println(router.dispatch(new Request("GET", "/admin/users", session, Map.of())));
        System.out.println(router.dispatch(new Request("GET", "/health", session, Map.of())));
    }
}
