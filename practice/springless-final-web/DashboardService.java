import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardService {
    // 擬似データ（本来はDBから取得）
    private final List<Attendance> attendanceHistory;

    public DashboardService() {
        List<Attendance> seed = new ArrayList<>();
        seed.add(new Attendance("2026-03-26", "-", "-", AttendanceStatus.NOT_CLOCKED_IN));
        seed.add(new Attendance("2026-03-25", "09:01", "18:12", AttendanceStatus.CLOCKED_OUT));
        seed.add(new Attendance("2026-03-24", "09:07", "17:55", AttendanceStatus.CLOCKED_OUT));
        this.attendanceHistory = Collections.unmodifiableList(seed);
    }

    public User getLoginUser() {
        return new User("user1", "ROLE_USER");
    }

    public Attendance getTodayAttendanceFor(String username) {
        return getLatestAttendanceFor(username);
    }

    public Attendance getLatestAttendanceFor(String username) {
        if (attendanceHistory.isEmpty()) {
            return new Attendance("-", "-", "-", AttendanceStatus.NOT_CLOCKED_IN);
        }
        return attendanceHistory.get(0);
    }

    public List<Attendance> getAttendancesFor(String username) {
        return attendanceHistory;
    }

    // action に応じたメッセージを返す（不正時は BusinessException）
    public String getTopMessageForAction(Attendance attendance, String action) {
        if (action == null || action.isBlank()) {
            return "メッセージ表示エリア（機能は後続Lessonで実装）";
        }

        return switch (action) {
            case "clock-in" -> validateClockIn(attendance);
            case "clock-out" -> validateClockOut(attendance);
            default -> throw new BusinessException("不正な操作です: " + action);
        };
    }

    private String validateClockIn(Attendance attendance) {
        return switch (attendance.getStatus()) {
            case NOT_CLOCKED_IN -> "出勤できます（次Lessonで実処理）";
            case WORKING -> throw new BusinessException("すでに出勤済みです");
            case CLOCKED_OUT -> throw new BusinessException("退勤済みのため再出勤できません");
        };
    }

    private String validateClockOut(Attendance attendance) {
        return switch (attendance.getStatus()) {
            case NOT_CLOCKED_IN -> throw new BusinessException("未出勤のため退勤できません");
            case WORKING -> "退勤できます（次Lessonで実処理）";
            case CLOCKED_OUT -> throw new BusinessException("すでに退勤済みです");
        };
    }
}