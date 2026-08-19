// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

// AttendanceテーブルのDB操作窓口
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // userId + workDate で当日レコードを検索
    // メソッド名からSQL相当の処理が自動生成される
    Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);
}