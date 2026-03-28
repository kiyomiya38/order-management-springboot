import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

enum AttendanceStatus {
    NOT_WORKING("未出勤"),
    WORKING("出勤中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }
}

class User {
    private final Long id;
    private final String name;

    User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    Long getId() {
        return id;
    }

    String getName() {
        return name;
    }
}

class Attendance {
    private final Long userId;
    private final LocalDate date;
    private AttendanceStatus status = AttendanceStatus.NOT_WORKING;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;

    Attendance(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    AttendanceStatus getStatus() {
        return status;
    }

    LocalDateTime getClockInAt() {
        return clockInAt;
    }

    LocalDateTime getClockOutAt() {
        return clockOutAt;
    }

    void clockIn(LocalDateTime now) {
        if (status != AttendanceStatus.NOT_WORKING) {
            throw new BusinessException("すでに出勤済みです。");
        }
        status = AttendanceStatus.WORKING;
        clockInAt = now;
    }

    void clockOut(LocalDateTime now) {
        if (status != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください。");
        }
        status = AttendanceStatus.FINISHED;
        clockOutAt = now;
    }
}

interface UserRepository {
    void save(User user);

    User findById(Long id);
}

class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public User findById(Long id) {
        return users.get(id);
    }
}

interface AttendanceRepository {
    Attendance findOrCreate(Long userId, LocalDate date);
}

class InMemoryAttendanceRepository implements AttendanceRepository {
    private final Map<String, Attendance> attendances = new HashMap<>();

    public Attendance findOrCreate(Long userId, LocalDate date) {
        String key = userId + "_" + date;
        if (!attendances.containsKey(key)) {
            attendances.put(key, new Attendance(userId, date));
        }
        return attendances.get(key);
    }
}

class AttendanceService {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    AttendanceService(UserRepository userRepository, AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
    }

    Attendance getTodayAttendance(Long userId) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessException("ユーザーが存在しません。");
        }
        return attendanceRepository.findOrCreate(userId, LocalDate.now());
    }

    void clockIn(Long userId) {
        getTodayAttendance(userId).clockIn(LocalDateTime.now());
    }

    void clockOut(Long userId) {
        getTodayAttendance(userId).clockOut(LocalDateTime.now());
    }
}

class AppConfig {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;

    AppConfig() {
        this.userRepository = new InMemoryUserRepository();
        this.attendanceRepository = new InMemoryAttendanceRepository();
        this.attendanceService = new AttendanceService(userRepository, attendanceRepository);

        userRepository.save(new User(1L, "Yamada"));
    }

    AttendanceService attendanceService() {
        return attendanceService;
    }
}

public class Lesson1Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        AttendanceService service = config.attendanceService();
        Long userId = 1L;

        printStatus(service, userId, "初期状態");
        service.clockIn(userId);
        printStatus(service, userId, "出勤後");
        service.clockOut(userId);
        printStatus(service, userId, "退勤後");
    }

    private static void printStatus(AttendanceService service, Long userId, String title) {
        Attendance attendance = service.getTodayAttendance(userId);
        System.out.println("=== " + title + " ===");
        System.out.println("状態: " + attendance.getStatus().getLabel());
        System.out.println("出勤: " + attendance.getClockInAt());
        System.out.println("退勤: " + attendance.getClockOutAt());
    }
}
