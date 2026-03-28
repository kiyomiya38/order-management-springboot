import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
    private AttendanceStatus status;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;

    Attendance(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
        this.status = AttendanceStatus.NOT_WORKING;
    }

    Long getUserId() {
        return userId;
    }

    LocalDate getDate() {
        return date;
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

    private String key(Long userId, LocalDate date) {
        return userId + "_" + date;
    }

    public Attendance findOrCreate(Long userId, LocalDate date) {
        String key = key(userId, date);
        if (!attendances.containsKey(key)) {
            attendances.put(key, new Attendance(userId, date));
        }
        return attendances.get(key);
    }
}

class VerboseAttendanceRepository implements AttendanceRepository {
    private final AttendanceRepository delegate;

    VerboseAttendanceRepository(AttendanceRepository delegate) {
        this.delegate = delegate;
    }

    public Attendance findOrCreate(Long userId, LocalDate date) {
        System.out.println("[REPO-LOG] findOrCreate userId=" + userId + ", date=" + date);
        return delegate.findOrCreate(userId, date);
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
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException("ユーザーが存在しません。");
        }
        return attendanceRepository.findOrCreate(userId, LocalDate.now());
    }

    void clockIn(Long userId) {
        Attendance attendance = getTodayAttendance(userId);
        attendance.clockIn(LocalDateTime.now());
    }

    void clockOut(Long userId) {
        Attendance attendance = getTodayAttendance(userId);
        attendance.clockOut(LocalDateTime.now());
    }
}

class ConsoleMenu {
    private final AttendanceService service;
    private final UserRepository userRepository;
    private final Long loginUserId;
    private final Scanner scanner = new Scanner(System.in);

    ConsoleMenu(AttendanceService service, UserRepository userRepository, Long loginUserId) {
        this.service = service;
        this.userRepository = userRepository;
        this.loginUserId = loginUserId;
    }

    void run() {
        System.out.println("ログイン中ユーザー: " + userRepository.findById(loginUserId).getName());
        boolean running = true;
        while (running) {
            printMenu();
            String command = scanner.nextLine();
            switch (command) {
                case "1":
                    showStatus();
                    break;
                case "2":
                    doClockIn();
                    break;
                case "3":
                    doClockOut();
                    break;
                case "9":
                    running = false;
                    System.out.println("終了します。");
                    break;
                default:
                    System.out.println("不明なコマンドです。1/2/3/9 を入力してください。");
            }
        }
    }

    private void printMenu() {
        System.out.println("");
        System.out.println("=== メニュー ===");
        System.out.println("1: 状態確認");
        System.out.println("2: 出勤");
        System.out.println("3: 退勤");
        System.out.println("9: 終了");
        System.out.print("コマンドを入力 > ");
    }

    private void showStatus() {
        try {
            Attendance attendance = service.getTodayAttendance(loginUserId);
            System.out.println("状態    : " + attendance.getStatus().getLabel());
            System.out.println("出勤時刻: " + attendance.getClockInAt());
            System.out.println("退勤時刻: " + attendance.getClockOutAt());
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }

    private void doClockIn() {
        try {
            service.clockIn(loginUserId);
            System.out.println("出勤を記録しました。");
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }

    private void doClockOut() {
        try {
            service.clockOut(loginUserId);
            System.out.println("退勤を記録しました。");
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }
}

public class Lesson3Main {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();

        // ここは「インターフェース型」で受けるのがポイント
        // 実装クラスは次のlessonの演習で差し替えます。
        AttendanceRepository attendanceRepository = new InMemoryAttendanceRepository();

        AttendanceService service = new AttendanceService(userRepository, attendanceRepository);

        userRepository.save(new User(1L, "Yamada"));

        ConsoleMenu menu = new ConsoleMenu(service, userRepository, 1L);
        menu.run();
    }
}
