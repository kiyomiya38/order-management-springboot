import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Attendance getTodayAttendance(String username) {
        return attendanceRepository.findTodayByUsername(username);
    }

    public void clockIn(String username) {
        Attendance current = attendanceRepository.findTodayByUsername(username);

        if (current.getStatus() != AttendanceStatus.NOT_CLOCKED_IN) {
            throw new BusinessException("すでに出勤済みです");
        }

        Attendance updated = new Attendance(
                current.getWorkDate(),
                nowTime(),
                "-",
                AttendanceStatus.WORKING
        );
        attendanceRepository.saveToday(username, updated);
    }

    public void clockOut(String username) {
        Attendance current = attendanceRepository.findTodayByUsername(username);

        if (current.getStatus() == AttendanceStatus.NOT_CLOCKED_IN) {
            throw new BusinessException("未出勤のため退勤できません");
        }
        if (current.getStatus() == AttendanceStatus.CLOCKED_OUT) {
            throw new BusinessException("すでに退勤済みです");
        }

        Attendance updated = new Attendance(
                current.getWorkDate(),
                current.getClockInTime(),
                nowTime(),
                AttendanceStatus.CLOCKED_OUT
        );
        attendanceRepository.saveToday(username, updated);
    }

    private String nowTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}