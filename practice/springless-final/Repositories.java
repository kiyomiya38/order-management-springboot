import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class UserRepository {
    private final Map<Long, User> usersById = new LinkedHashMap<>();
    private long sequence = 1;

    synchronized User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence++);
        }
        usersById.put(user.getId(), user);
        return user;
    }

    User findByUsername(String username) {
        for (User user : usersById.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    User findById(Long id) {
        return usersById.get(id);
    }

    boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    void deleteById(Long id) {
        usersById.remove(id);
    }

    long count() {
        return usersById.size();
    }
}

class AttendanceRepository {
    private final Map<Long, Attendance> attendancesById = new LinkedHashMap<>();
    private long sequence = 1;

    synchronized Attendance save(Attendance attendance) {
        if (attendance.getId() == null) {
            attendance.setId(sequence++);
            attendance.setCreatedAt(LocalDateTime.now());
        }
        attendance.setUpdatedAt(LocalDateTime.now());
        attendancesById.put(attendance.getId(), attendance);
        return attendance;
    }

    Attendance findById(Long id) {
        return attendancesById.get(id);
    }

    Attendance findByUserIdAndWorkDate(Long userId, LocalDate workDate) {
        for (Attendance attendance : attendancesById.values()) {
            if (attendance.getUser().getId().equals(userId)
                    && attendance.getWorkDate().equals(workDate)) {
                return attendance;
            }
        }
        return null;
    }

    List<Attendance> findByUserIdOrderByWorkDateDesc(Long userId) {
        List<Attendance> list = new ArrayList<>();
        for (Attendance attendance : attendancesById.values()) {
            if (attendance.getUser().getId().equals(userId)) {
                list.add(attendance);
            }
        }
        list.sort(Comparator.comparing(Attendance::getWorkDate).reversed()
                .thenComparing(Attendance::getId, Comparator.reverseOrder()));
        return list;
    }

    List<Attendance> findAllByOrderByWorkDateDesc() {
        List<Attendance> list = new ArrayList<>(attendancesById.values());
        list.sort(Comparator.comparing(Attendance::getWorkDate).reversed()
                .thenComparing(Attendance::getId, Comparator.reverseOrder()));
        return list;
    }
}

class SessionData {
    private String username;
    private FlashData flashData = new FlashData();

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    FlashData getFlashData() {
        return flashData;
    }

    void setFlashData(FlashData flashData) {
        this.flashData = flashData;
    }
}

class SessionStore {
    private final Map<String, SessionData> sessions = new HashMap<>();

    String create(String username) {
        String sid = UUID.randomUUID().toString();
        SessionData data = new SessionData();
        data.setUsername(username);
        sessions.put(sid, data);
        return sid;
    }

    SessionData find(String sid) {
        return sessions.get(sid);
    }

    void remove(String sid) {
        sessions.remove(sid);
    }

    void putFlash(String sid, String message, String error) {
        SessionData data = sessions.get(sid);
        if (data == null) {
            return;
        }
        FlashData flash = new FlashData();
        flash.setMessage(message);
        flash.setError(error);
        data.setFlashData(flash);
    }

    FlashData consumeFlash(String sid) {
        SessionData data = sessions.get(sid);
        if (data == null) {
            return new FlashData();
        }
        FlashData current = data.getFlashData();
        data.setFlashData(new FlashData());
        return current == null ? new FlashData() : current;
    }
}
