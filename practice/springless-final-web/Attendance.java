public class Attendance {
    private final String workDate;
    private final String clockInTime;
    private final String clockOutTime;
    private final AttendanceStatus status;

    public Attendance(String workDate, String clockInTime, String clockOutTime, AttendanceStatus status) {
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.status = status;
    }

    public String getWorkDate() {
        return workDate;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    // 画面表示用ラベル
    public String getStatusLabel() {
        return status.getLabel();
    }
}