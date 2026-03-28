import com.sun.net.httpserver.HttpExchange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class AuthService {
    private final UserRepository userRepository;
    private final SessionStore sessionStore;

    AuthService(UserRepository userRepository, SessionStore sessionStore) {
        this.userRepository = userRepository;
        this.sessionStore = sessionStore;
    }

    String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        return sessionStore.create(username);
    }

    void logoutBySessionId(String sid) {
        if (sid != null && !sid.isBlank()) {
            sessionStore.remove(sid);
        }
    }

    User currentUser(HttpExchange exchange) {
        String sid = WebUtil.getCookie(exchange, "sid");
        if (sid == null) {
            return null;
        }
        SessionData data = sessionStore.find(sid);
        if (data == null) {
            return null;
        }
        return userRepository.findByUsername(data.getUsername());
    }
}

class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("ユーザーが存在しません");
        }
        return user;
    }

    List<User> list() {
        return userRepository.findAll();
    }

    User get(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new BusinessException("ユーザーが存在しません");
        }
        return user;
    }

    User create(String username, String rawPassword, String role) {
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            throw new BusinessException("ユーザー名が既に存在します");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(rawPassword);
        user.setRole(role);
        return userRepository.save(user);
    }

    User update(Long id, String username, String rawPassword, String role) {
        User user = get(id);
        User existing = userRepository.findByUsername(username);
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("ユーザー名が既に存在します");
        }
        user.setUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(rawPassword);
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    void delete(Long id) {
        userRepository.deleteById(id);
    }
}

class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    Attendance getTodayAttendance(Long userId) {
        return attendanceRepository.findByUserIdAndWorkDate(userId, LocalDate.now());
    }

    Attendance getAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id);
        if (attendance == null) {
            throw new BusinessException("勤怠が存在しません");
        }
        return attendance;
    }

    List<Attendance> listAttendances(Long userId) {
        return attendanceRepository.findByUserIdOrderByWorkDateDesc(userId);
    }

    List<Attendance> listAllAttendances() {
        return attendanceRepository.findAllByOrderByWorkDateDesc();
    }

    Attendance clockIn(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance existing = attendanceRepository.findByUserIdAndWorkDate(userId, today);
        if (existing != null) {
            throw new BusinessException("すでに出勤済みです");
        }
        User user = getUser(userId);
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setWorkDate(today);
        attendance.setStartTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.WORKING);
        return attendanceRepository.save(attendance);
    }

    Attendance clockOut(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndWorkDate(userId, today);
        if (attendance == null) {
            throw new BusinessException("退勤するには先に出勤してください");
        }
        if (attendance.getStatus() == AttendanceStatus.FINISHED) {
            throw new BusinessException("すでに退勤済みです");
        }
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤するには先に出勤してください");
        }
        attendance.setEndTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.FINISHED);
        return attendanceRepository.save(attendance);
    }

    Attendance updateAttendance(Long attendanceId,
                                Long userId,
                                LocalDate workDate,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                AttendanceStatus status) {
        Attendance attendance = getAttendance(attendanceId);
        User user = getUser(userId);

        Attendance existing = attendanceRepository.findByUserIdAndWorkDate(userId, workDate);
        if (existing != null && !existing.getId().equals(attendanceId)) {
            throw new BusinessException("同じ日付の勤怠が既に存在します");
        }

        validateStatusAndTimes(status, startTime, endTime);

        attendance.setUser(user);
        attendance.setWorkDate(workDate);
        attendance.setStartTime(startTime);
        attendance.setEndTime(endTime);
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    private User getUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException("ユーザーが存在しません");
        }
        return user;
    }

    private void validateStatusAndTimes(AttendanceStatus status,
                                        LocalDateTime startTime,
                                        LocalDateTime endTime) {
        if (status == AttendanceStatus.NOT_STARTED) {
            if (startTime != null || endTime != null) {
                throw new BusinessException("未出勤の時刻は空にしてください");
            }
            return;
        }
        if (status == AttendanceStatus.WORKING) {
            if (startTime == null || endTime != null) {
                throw new BusinessException("出勤中は開始時刻のみ必要です");
            }
            return;
        }
        if (status == AttendanceStatus.FINISHED) {
            if (startTime == null || endTime == null) {
                throw new BusinessException("退勤済みは開始・終了時刻が必要です");
            }
        }
    }
}

class Validation {
    private final List<String> errors = new ArrayList<>();

    void add(String message) {
        errors.add(message);
    }

    boolean hasErrors() {
        return !errors.isEmpty();
    }

    List<String> errors() {
        return errors;
    }
}
