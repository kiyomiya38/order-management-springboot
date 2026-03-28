import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

class UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    void save(User user) {
        users.put(user.getId(), user);
    }

    User findById(Long id) {
        return users.get(id);
    }
}

class AttendanceRepository {
    private final Map<String, Attendance> attendances = new HashMap<>();

    private String key(Long userId, LocalDate date) {
        return userId + "_" + date;
    }

    Attendance findOrCreate(Long userId, LocalDate date) {
        String key = key(userId, date);
        if (!attendances.containsKey(key)) {
            attendances.put(key, new Attendance(userId, date));
        }
        return attendances.get(key);
    }

    List<Attendance> findAllByDate(LocalDate date) {
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
}

public class Lesson2Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        AttendanceRepository attendanceRepository = new AttendanceRepository();
        AttendanceService service = new AttendanceService(userRepository, attendanceRepository);

        userRepository.save(new User(1L, "Yamada"));
        userRepository.save(new User(2L, "Suzuki"));
        userRepository.save(new User(3L, "Tanaka"));

        service.clockIn(1L);
        service.clockOut(1L);
        service.clockIn(2L);

        printDailySummary(service, userRepository, LocalDate.now());
    }

    private static void printDailySummary(AttendanceService service, UserRepository userRepository, LocalDate date) {
        System.out.println("=== " + date + " の勤怠一覧 ===");
        List<Attendance> attendances = service.getDailyAttendances(date);
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
}
