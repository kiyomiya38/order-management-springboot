package com.shinesoft.attendance.web.form; // 管理者フォームのパッケージ

import java.time.LocalDate; // 勤務日
import java.time.LocalDateTime; // 出勤/退勤時刻

import org.springframework.format.annotation.DateTimeFormat; // 文字列->日時変換

import jakarta.validation.constraints.NotNull; // 必須入力チェック

import com.shinesoft.attendance.domain.AttendanceStatus; // 状態Enum

public class AdminAttendanceForm {
    @NotNull // ユーザーID必須
    private Long userId;

    @NotNull // 勤務日必須
    @DateTimeFormat(pattern = "yyyy-MM-dd") // date input形式
    private LocalDate workDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // datetime-local input形式
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // datetime-local input形式
    private LocalDateTime endTime;

    @NotNull // 状態必須
    private AttendanceStatus status;

    private String username; // 画面表示専用（保存対象ではない）

    public Long getUserId() { return userId; }
    public LocalDate getWorkDate() { return workDate; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public AttendanceStatus getStatus() { return status; }
    public String getUsername() { return username; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public void setUsername(String username) { this.username = username; }
}