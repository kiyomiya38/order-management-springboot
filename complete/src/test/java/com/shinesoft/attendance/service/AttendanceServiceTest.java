package com.shinesoft.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttendanceServiceTest {

    @Autowired
    private AttendanceService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        User user = userRepository.findByUsername("user1").orElseGet(() -> {
            User u = new User();
            u.setUsername("user1");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRole("ROLE_USER");
            return userRepository.save(u);
        });
        userId = user.getId();
    }

    @Test
    void clockIn_success() {
        Attendance attendance = service.clockIn(userId);
        assertEquals(AttendanceStatus.WORKING, attendance.getStatus());
        assertNotNull(attendance.getStartTime());
    }

    @Test
    void clockIn_twice_shouldFail() {
        service.clockIn(userId);
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockIn(userId));
        assertEquals("すでに出勤済みです", ex.getMessage());
    }

    @Test
    void clockOut_beforeClockIn_shouldFail() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
        assertEquals("退勤するには先に出勤してください", ex.getMessage());
    }

    @Test
    void clockOut_twice_shouldFail() {
        service.clockIn(userId);
        service.clockOut(userId);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.clockOut(userId));
        assertEquals("すでに退勤済みです", ex.getMessage());
    }

    @Test
    void updateAttendance_endBeforeStart_shouldFail() {
        Attendance attendance = service.clockIn(userId);
        LocalDate workDate = LocalDate.now();
        LocalDateTime start = workDate.atTime(9, 0);
        LocalDateTime end = workDate.atTime(8, 59);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            service.updateAttendance(
                attendance.getId(), userId, workDate, start, end, AttendanceStatus.FINISHED));

        assertEquals("終了時刻は開始時刻以降にしてください", ex.getMessage());
    }

    @Test
    void updateAttendance_startDateMustMatchWorkDate() {
        Attendance attendance = service.clockIn(userId);
        LocalDate workDate = LocalDate.now().minusDays(1);
        LocalDateTime start = LocalDate.now().atTime(9, 0);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            service.updateAttendance(
                attendance.getId(), userId, workDate, start, null, AttendanceStatus.WORKING));

        assertEquals("開始時刻の日付は勤務日と一致させてください", ex.getMessage());
    }
}
