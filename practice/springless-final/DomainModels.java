import java.time.LocalDate;
import java.time.LocalDateTime;

enum AttendanceStatus {
    NOT_STARTED("未出勤"),
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

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

class User {
    private Long id;
    private String username;
    private String password;
    private String role;

    Long getId() {
        return id;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getRole() {
        return role;
    }

    void setId(Long id) {
        this.id = id;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setRole(String role) {
        this.role = role;
    }
}

class Attendance {
    private Long id;
    private User user;
    private LocalDate workDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AttendanceStatus status = AttendanceStatus.NOT_STARTED;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Long getId() {
        return id;
    }

    User getUser() {
        return user;
    }

    LocalDate getWorkDate() {
        return workDate;
    }

    LocalDateTime getStartTime() {
        return startTime;
    }

    LocalDateTime getEndTime() {
        return endTime;
    }

    AttendanceStatus getStatus() {
        return status;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    void setId(Long id) {
        this.id = id;
    }

    void setUser(User user) {
        this.user = user;
    }

    void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

class UserForm {
    private String username;
    private String password;
    private String role;

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getRole() {
        return role;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setRole(String role) {
        this.role = role;
    }
}

class AdminAttendanceForm {
    private Long userId;
    private String username;
    private LocalDate workDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AttendanceStatus status;

    Long getUserId() {
        return userId;
    }

    String getUsername() {
        return username;
    }

    LocalDate getWorkDate() {
        return workDate;
    }

    LocalDateTime getStartTime() {
        return startTime;
    }

    LocalDateTime getEndTime() {
        return endTime;
    }

    AttendanceStatus getStatus() {
        return status;
    }

    void setUserId(Long userId) {
        this.userId = userId;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}

class FlashData {
    private String error;
    private String message;

    String getError() {
        return error;
    }

    String getMessage() {
        return message;
    }

    void setError(String error) {
        this.error = error;
    }

    void setMessage(String message) {
        this.message = message;
    }
}
