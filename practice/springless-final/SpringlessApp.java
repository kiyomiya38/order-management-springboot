import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpringlessApp {
    private static class AppContext {
        final AuthController authController;
        final HomeController homeController;
        final AttendanceController attendanceController;
        final UserController userController;
        final AdminAttendanceController adminAttendanceController;
        final String stylesCss;

        AppContext() throws IOException {
            UserRepository userRepository = new UserRepository();
            AttendanceRepository attendanceRepository = new AttendanceRepository();
            SessionStore sessionStore = new SessionStore();

            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);

                User user = new User();
                user.setUsername("user1");
                user.setPassword("password");
                user.setRole("ROLE_USER");
                userRepository.save(user);
            }

            AuthService authService = new AuthService(userRepository, sessionStore);
            UserService userService = new UserService(userRepository);
            AttendanceService attendanceService = new AttendanceService(attendanceRepository, userRepository);

            this.authController = new AuthController(authService);
            this.homeController = new HomeController(authService, sessionStore, attendanceService, userService);
            this.attendanceController = new AttendanceController(authService, sessionStore, attendanceService, userService);
            this.userController = new UserController(authService, sessionStore, userService);
            this.adminAttendanceController =
                    new AdminAttendanceController(authService, sessionStore, attendanceService, userService);

            this.stylesCss = Files.readString(Path.of("styles.css"), StandardCharsets.UTF_8);
        }
    }

    private static class AppHandler {
        private final AppContext ctx;

        AppHandler(AppContext ctx) {
            this.ctx = ctx;
        }

        void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if ("GET".equals(method) && "/styles.css".equals(path)) {
                    WebUtil.sendCss(exchange, 200, ctx.stylesCss);
                    return;
                }
                if ("GET".equals(method) && "/login".equals(path)) {
                    ctx.authController.getLogin(exchange);
                    return;
                }
                if ("POST".equals(method) && "/login".equals(path)) {
                    ctx.authController.postLogin(exchange);
                    return;
                }
                if ("POST".equals(method) && "/logout".equals(path)) {
                    ctx.authController.postLogout(exchange);
                    return;
                }
                if ("GET".equals(method) && "/".equals(path)) {
                    ctx.homeController.getIndex(exchange);
                    return;
                }
                if ("POST".equals(method) && "/clock-in".equals(path)) {
                    ctx.homeController.postClockIn(exchange);
                    return;
                }
                if ("POST".equals(method) && "/clock-out".equals(path)) {
                    ctx.homeController.postClockOut(exchange);
                    return;
                }
                if ("GET".equals(method) && "/attendances".equals(path)) {
                    ctx.attendanceController.list(exchange);
                    return;
                }
                if ("GET".equals(method) && "/users".equals(path)) {
                    ctx.userController.list(exchange);
                    return;
                }
                if ("GET".equals(method) && "/users/new".equals(path)) {
                    ctx.userController.newForm(exchange);
                    return;
                }
                if ("POST".equals(method) && "/users".equals(path)) {
                    ctx.userController.create(exchange);
                    return;
                }
                if ("GET".equals(method) && "/admin/attendances".equals(path)) {
                    ctx.adminAttendanceController.list(exchange);
                    return;
                }

                Long id = pathId(path, "/users/", "/edit");
                if (id != null && "GET".equals(method)) {
                    ctx.userController.editForm(exchange, id);
                    return;
                }

                id = pathId(path, "/users/", "/delete");
                if (id != null && "POST".equals(method)) {
                    ctx.userController.delete(exchange, id);
                    return;
                }

                id = pathId(path, "/users/", "");
                if (id != null && "POST".equals(method)) {
                    ctx.userController.update(exchange, id);
                    return;
                }

                id = pathId(path, "/admin/attendances/", "/edit");
                if (id != null && "GET".equals(method)) {
                    ctx.adminAttendanceController.editForm(exchange, id);
                    return;
                }

                id = pathId(path, "/admin/attendances/", "");
                if (id != null && "POST".equals(method)) {
                    ctx.adminAttendanceController.update(exchange, id);
                    return;
                }

                WebUtil.sendText(exchange, 404, "Not Found");
            } catch (Exception ex) {
                WebUtil.sendText(exchange, 500, "Internal Server Error: " + ex.getMessage());
            }
        }

        private Long pathId(String path, String prefix, String suffix) {
            if (!path.startsWith(prefix)) {
                return null;
            }
            if (!suffix.isEmpty() && !path.endsWith(suffix)) {
                return null;
            }
            int endIndex = suffix.isEmpty() ? path.length() : path.length() - suffix.length();
            if (endIndex <= prefix.length()) {
                return null;
            }
            String idPart = path.substring(prefix.length(), endIndex);
            if (idPart.contains("/")) {
                return null;
            }
            try {
                return Long.parseLong(idPart);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        AppContext context = new AppContext();
        AppHandler handler = new AppHandler(context);

        int port = 8080;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                port = 8080;
            }
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", handler::handle);
        server.setExecutor(null);
        server.start();

        System.out.println("Springless final app started: http://localhost:" + port);
        System.out.println("Login users: admin/admin123, user1/password");
    }
}
