// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

// AttendanceテーブルのDB操作窓口
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // userId + 勤務日で当日レコードを1件取得（出勤/退勤判定に使う）
    Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);

    // 指定ユーザーの履歴を勤務日降順で取得
    // 直近データを上に表示したい一覧画面向け
    List<Attendance> findByUser_IdOrderByWorkDateDesc(Long userId);

    // 管理者向けに全ユーザー分の履歴を勤務日降順で取得
    @EntityGraph(attributePaths = "user")
    List<Attendance> findAllByOrderByWorkDateDesc();

    // 管理者編集でユーザー情報も同時取得
    @EntityGraph(attributePaths = "user")
    Optional<Attendance> findWithUserById(Long id);

    // ユーザー削除前の参照整合チェック
    boolean existsByUser_Id(Long userId);
}