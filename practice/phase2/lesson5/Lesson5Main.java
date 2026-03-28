import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    List<User> findAll();
}

class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}

interface AttendanceRepository {
    Attendance findOrCreate(Long userId, LocalDate date);

    List<Attendance> findAllByDate(LocalDate date);
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

    public List<Attendance> findAllByDate(LocalDate date) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance attendance : attendances.values()) {
            if (attendance.getDate().equals(date)) {
                result.add(attendance);
            }
        }
        return result;
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

    List<Attendance> getDailyAttendances(LocalDate date) {
        return attendanceRepository.findAllByDate(date);
    }

    int countWorkingUsers(LocalDate date) {
        int count = 0;
        List<Attendance> attendances = getDailyAttendances(date);
        for (Attendance attendance : attendances) {
            if (attendance.getStatus() == AttendanceStatus.WORKING) {
                count++;
            }
        }
        return count;
    }
}

class ConsoleMenu {
    private final AttendanceService service;
    private final UserRepository userRepository;
    private final Scanner scanner = new Scanner(System.in);
    private Long currentUserId;

    ConsoleMenu(AttendanceService service, UserRepository userRepository, Long initialUserId) {
        this.service = service;
        this.userRepository = userRepository;
        this.currentUserId = initialUserId;
    }

    void run() {
        boolean running = true;
        while (running) {
            printCurrentUser();
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
                case "4":
                    switchUser();
                    break;
                case "5":
                    showDailySummary();
                    break;
                case "6":
                    showWorkingCount();
                    break;
                case "9":
                    running = false;
                    System.out.println("終了します。");
                    break;
                default:
                    System.out.println("不明なコマンドです。1/2/3/4/5/6/9 を入力してください。");
            }
        }
    }

    private void printCurrentUser() {
        User user = userRepository.findById(currentUserId);
        String userLabel = (user == null) ? "(不明)" : user.getName();
        System.out.println("");
        System.out.println("現在ユーザー: " + currentUserId + " / " + userLabel);
    }

    private void printMenu() {
        System.out.println("=== メニュー ===");
        System.out.println("1: 状態確認");
        System.out.println("2: 出勤");
        System.out.println("3: 退勤");
        System.out.println("4: ユーザー切り替え");
        System.out.println("5: 本日の勤怠一覧");
        System.out.println("6: 出勤中人数");
        System.out.println("9: 終了");
        System.out.print("コマンドを入力 > ");
    }

    private void showStatus() {
        try {
            Attendance attendance = service.getTodayAttendance(currentUserId);
            System.out.println("状態    : " + attendance.getStatus().getLabel());
            System.out.println("出勤時刻: " + attendance.getClockInAt());
            System.out.println("退勤時刻: " + attendance.getClockOutAt());
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }

    private void doClockIn() {
        try {
            service.clockIn(currentUserId);
            System.out.println("出勤を記録しました。");
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }

    private void doClockOut() {
        try {
            service.clockOut(currentUserId);
            System.out.println("退勤を記録しました。");
        } catch (BusinessException e) {
            System.out.println("[業務エラー] " + e.getMessage());
        }
    }

    private void switchUser() {
        System.out.println("切り替え可能ユーザー:");
        for (User user : userRepository.findAll()) {
            System.out.println("- " + user.getId() + " : " + user.getName());
        }
        System.out.print("切り替え先ユーザーIDを入力 > ");
        String raw = scanner.nextLine();
        Long nextUserId;
        try {
            nextUserId = Long.parseLong(raw);
        } catch (NumberFormatException e) {
            System.out.println("[入力エラー] 数字で入力してください。");
            return;
        }

        if (userRepository.findById(nextUserId) == null) {
            System.out.println("[入力エラー] そのユーザーIDは存在しません。");
            return;
        }

        currentUserId = nextUserId;
        System.out.println("ユーザーを切り替えました。");
    }

    private void showDailySummary() {
        LocalDate today = LocalDate.now();
        List<Attendance> attendances = service.getDailyAttendances(today);
        System.out.println("=== " + today + " の勤怠一覧 ===");
        if (attendances.isEmpty()) {
            System.out.println("(まだ打刻データがありません)");
            return;
        }

        for (Attendance attendance : attendances) {
            User user = userRepository.findById(attendance.getUserId());
            String userName = (user == null) ? "(不明ユーザー)" : user.getName();
            System.out.println(
                    userName
                            + " / 状態: " + attendance.getStatus().getLabel()
                            + " / 出勤: " + attendance.getClockInAt()
                            + " / 退勤: " + attendance.getClockOutAt());
        }
    }

    private void showWorkingCount() {
        int count = service.countWorkingUsers(LocalDate.now());
        System.out.println("出勤中人数: " + count + "人");
    }
}

public class Lesson5Main {
    public static void main(String[] args) {
        UserRepository userRepository = new InMemoryUserRepository();
        AttendanceRepository attendanceRepository = new InMemoryAttendanceRepository();
        AttendanceService service = new AttendanceService(userRepository, attendanceRepository);

        userRepository.save(new User(1L, "Yamada"));
        userRepository.save(new User(2L, "Suzuki"));
        userRepository.save(new User(3L, "Tanaka"));

        service.clockIn(1L);
        service.clockOut(1L);
        service.clockIn(2L);

        ConsoleMenu menu = new ConsoleMenu(service, userRepository, 1L);
        menu.run();
    }
}
