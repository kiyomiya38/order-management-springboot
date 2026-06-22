package com.shinesoft.attendance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private User user;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        user = userRepository.findByUsername("user1").orElseThrow();
    }

    @Test
    void create_rejectsInvalidPassword() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.create("short-password-user", "short", "ROLE_USER"));
        assertEquals("パスワードは8〜64文字にしてください", ex.getMessage());
    }

    @Test
    void delete_rejectsUserWithAttendanceHistory() {
        attendanceService.clockIn(user.getId());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> userService.delete(user.getId()));
        assertEquals("勤怠履歴があるユーザーは削除できません", ex.getMessage());
    }

    @Test
    void delete_removesUserWithoutAttendanceHistory() {
        User created = userService.create("delete-target", "password123", "ROLE_USER");

        userService.delete(created.getId());

        assertFalse(userRepository.existsById(created.getId()));
    }
}
