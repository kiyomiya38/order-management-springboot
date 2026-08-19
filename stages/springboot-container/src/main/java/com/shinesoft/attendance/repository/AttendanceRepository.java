package com.shinesoft.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUser_IdAndWorkDate(Long userId, LocalDate workDate);
    List<Attendance> findByUser_IdOrderByWorkDateDesc(Long userId);

    @EntityGraph(attributePaths = "user")
    List<Attendance> findAllByOrderByWorkDateDesc();

    @EntityGraph(attributePaths = "user")
    Optional<Attendance> findWithUserById(Long id);

    boolean existsByUser_Id(Long userId);
}
