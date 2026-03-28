import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 手動DI: 依存オブジェクトをAppで作って渡す
        DashboardService dashboardService = new DashboardService();
        AttendanceRepository attendanceRepository = new InMemoryAttendanceRepository();
        AttendanceService attendanceService = new AttendanceService(attendanceRepository);
        PageHandlers handlers = new PageHandlers(dashboardService, attendanceService);

        server.createContext("/", handlers::handleTop);
        server.createContext("/health", handlers::handleHealth);
        server.createContext("/login", handlers::handleLogin);
        server.createContext("/attendances", handlers::handleAttendances);
        server.createContext("/users", handlers::handleUsers);
        server.createContext("/admin/attendances", handlers::handleAdminAttendances);
        server.createContext("/clock-in", handlers::handleClockIn);
        server.createContext("/clock-out", handlers::handleClockOut);

        server.setExecutor(null);
        server.start();

        System.out.println("Server started: http://localhost:8080");
    }
}