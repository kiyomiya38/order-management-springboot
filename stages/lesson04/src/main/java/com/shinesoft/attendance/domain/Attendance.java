// Entityクラスを置くパッケージ
package com.shinesoft.attendance.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// 勤怠テーブルに対応するEntity
@Entity
@Table(
    name = "attendances",
    // DB制約: 1ユーザー1日1件（同日の二重出勤をDBレベルでも防ぐ）
    uniqueConstraints = @UniqueConstraint(name = "uk_attendance_user_date", columnNames = {"user_id", "work_date"})
)
public class Attendance {
    // 主キー
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多対1: 多数の勤怠レコードが1ユーザーに紐づく
    // LAZY: 必要になるまでuser本体は読み込まない
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // 外部キー列名
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 勤務日（yyyy-MM-dd）
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    // 出勤時刻（未出勤ならnull）
    @Column(name = "start_time")
    private LocalDateTime startTime;

    // 退勤時刻（退勤前ならnull）
    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Enumを文字列として保存（NOT_STARTED / WORKING / FINISHED）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    // 監査用の作成日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 監査用の更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // INSERT前に自動実行されるコールバック
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // UPDATE前に自動実行されるコールバック
    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 以下はアクセサ（getter/setter）
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}