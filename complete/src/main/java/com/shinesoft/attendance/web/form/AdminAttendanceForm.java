package com.shinesoft.attendance.web.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

import com.shinesoft.attendance.domain.AttendanceStatus;

public class AdminAttendanceForm {
    @NotNull
    private Long userId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotNull
    private AttendanceStatus status;

    private String username;

    public Long getUserId() {
        return userId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
