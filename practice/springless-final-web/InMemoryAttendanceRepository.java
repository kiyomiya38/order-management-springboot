import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAttendanceRepository implements AttendanceRepository {
    private final Map<String, Attendance> store = new HashMap<>();

    @Override
    public Attendance findTodayByUsername(String username) {
        Attendance existing = store.get(username);
        if (existing != null) {
            return existing;
        }

        // 初回アクセス時の初期状態
        return new Attendance(
                LocalDate.now().toString(),
                "-",
                "-",
                AttendanceStatus.NOT_CLOCKED_IN
        );
    }

    @Override
    public void saveToday(String username, Attendance attendance) {
        store.put(username, attendance);
    }
}