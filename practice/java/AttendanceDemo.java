public class AttendanceDemo {
    public static void main(String[] args) {
        Attendance a = new Attendance();
        System.out.println("状態: " + a.status);

        a.clockOut();   // 未出勤で退勤
        a.clockIn();    // 出勤
        a.clockIn();    // 二重出勤
        a.clockOut();   // 退勤

        System.out.println("状態: " + a.status);
    }
}