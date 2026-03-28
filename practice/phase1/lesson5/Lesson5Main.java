enum AttendanceStatus {
    NOT_WORKING("未出勤"),
    WORKING("出勤中"),
    ON_BREAK("休憩中"),
    FINISHED("退勤済み");

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }
}

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

class User {
    private final String employeeCode;
    private final String name;

    User(String employeeCode, String name) {
        this.employeeCode = employeeCode;
        this.name = name;
    }

    String displayName() {
        return "[" + employeeCode + "] " + name;
    }
}

class Attendance {
    private final User user;
    private AttendanceStatus status;
    private int breakCount;

    Attendance(User user) {
        this.user = user;
        this.status = AttendanceStatus.NOT_WORKING;
        this.breakCount = 0;
    }

    User getUser() {
        return user;
    }

    AttendanceStatus getStatus() {
        return status;
    }

    int getBreakCount() {
        return breakCount;
    }

    void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    void incrementBreakCount() {
        this.breakCount++;
    }
}

class AttendanceService {
    private final Attendance attendance;

    AttendanceService(Attendance attendance) {
        this.attendance = attendance;
    }

    Attendance getAttendance() {
        return attendance;
    }

    void clockIn() {
        if (attendance.getStatus() != AttendanceStatus.NOT_WORKING) {
            throw new BusinessException("出勤できるのは未出勤のときだけです");
        }
        attendance.setStatus(AttendanceStatus.WORKING);
    }

    void startBreak() {
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("休憩開始できるのは出勤中のときだけです");
        }
        attendance.setStatus(AttendanceStatus.ON_BREAK);
    }

    void endBreak() {
        if (attendance.getStatus() != AttendanceStatus.ON_BREAK) {
            throw new BusinessException("休憩終了できるのは休憩中のときだけです");
        }
        attendance.setStatus(AttendanceStatus.WORKING);
        attendance.incrementBreakCount();
    }

    void clockOut() {
        if (attendance.getStatus() != AttendanceStatus.WORKING) {
            throw new BusinessException("退勤できるのは出勤中のときだけです");
        }
        attendance.setStatus(AttendanceStatus.FINISHED);
    }
}

public class Lesson5Main {
    private static void printState(AttendanceService service) {
        Attendance attendance = service.getAttendance();
        System.out.println(
                attendance.getUser().displayName()
                        + " / 状態: " + attendance.getStatus().getLabel()
                        + " / 休憩回数: " + attendance.getBreakCount());
    }

    public static void main(String[] args) {
        User user = new User("U001", "Yamada");
        Attendance attendance = new Attendance(user);
        AttendanceService service = new AttendanceService(attendance);

        try {
            printState(service);

            service.clockOut();
            service.clockIn();
            printState(service);

            service.startBreak();
            printState(service);

            service.endBreak();
            service.startBreak();
            service.endBreak();
            printState(service);

            service.clockOut();
            printState(service);

            // 異常な流れ（退勤後に再度退勤）
            service.clockOut();
        } catch (BusinessException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
}
