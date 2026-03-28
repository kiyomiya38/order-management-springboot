public interface AttendanceRepository {
    Attendance findTodayByUsername(String username);

    void saveToday(String username, Attendance attendance);
}