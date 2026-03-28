import java.util.LinkedHashMap;
import java.util.Map;

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

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

class UserRepository {
    private final Map<String, User> users = new LinkedHashMap<>();

    UserRepository() {
        users.put("admin", new User("admin", "管理者", "ADMIN"));
    }

    boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    void save(User user) {
        users.put(user.getUsername(), user);
    }

    Map<String, User> findAll() {
        return users;
    }
}

class UserCreateForm {
    String username;
    String displayName;
    String role;
}

class UserCreateRequest {
    private final String username;
    private final String displayName;
    private final String role;

    UserCreateRequest(String username, String displayName, String role) {
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
        } else if (!form.username.matches("[a-z0-9_]+")) {
            result.addError("username", "username は半角小文字・数字・_ で入力してください。");
        } else if (userRepository.existsByUsername(form.username)) {
            result.addError("username", "その username は既に使われています。");
        }

        if (form.displayName == null || form.displayName.isBlank()) {
            result.addError("displayName", "displayName は必須です。");
        } else if (form.displayName.length() > 30) {
            result.addError("displayName", "displayName は30文字以内で入力してください。");
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

    void createUser(UserCreateRequest request) {
        userRepository.save(new User(request.getUsername(), request.getDisplayName(), request.getRole()));
    }

    Map<String, User> findAll() {
        return userRepository.findAll();
    }
}

class AdminUserController {
    private final UserCreateValidator validator;
    private final UserService userService;

    AdminUserController(UserCreateValidator validator, UserService userService) {
        this.validator = validator;
        this.userService = userService;
    }

    String create(UserCreateForm form) {
        ValidationResult result = validator.validate(form);
        if (result.hasErrors()) {
            return "validation error: " + result.getErrors();
        }
        UserCreateRequest request = new UserCreateRequest(form.username, form.displayName, form.role);
        userService.createUser(request);
        return "created: " + request.getUsername();
    }
}

public class Lesson3Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        UserCreateValidator validator = new UserCreateValidator(userRepository);
        UserService userService = new UserService(userRepository);
        AdminUserController controller = new AdminUserController(validator, userService);

        UserCreateForm invalid = new UserCreateForm();
        invalid.username = "Admin";
        invalid.displayName = "";
        invalid.role = "MANAGER";
        System.out.println(controller.create(invalid));

        UserCreateForm valid = new UserCreateForm();
        valid.username = "tanaka";
        valid.displayName = "田中";
        valid.role = "USER";
        System.out.println(controller.create(valid));

        System.out.println("users: " + userService.findAll().keySet());
    }
}
