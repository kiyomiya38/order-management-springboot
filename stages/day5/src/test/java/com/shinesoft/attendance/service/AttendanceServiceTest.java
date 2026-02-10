package com.shinesoft.attendance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.Attendance;
import com.shinesoft.attendance.domain.AttendanceStatus;
import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
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
}
