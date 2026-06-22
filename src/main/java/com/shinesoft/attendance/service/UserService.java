package com.shinesoft.attendance.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.repository.AttendanceRepository;
import com.shinesoft.attendance.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AttendanceRepository attendanceRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public User get(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません"));
    }

    @Transactional
    public User create(String username, String rawPassword, String role) {
        username = validateUsername(username);
        validatePassword(rawPassword);
        validateRole(role);
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("ユーザー名が既に存在します");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, String username, String rawPassword, String role) {
        username = validateUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            validatePassword(rawPassword);
        }
        validateRole(role);
        User user = get(id);
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException("ユーザー名が既に存在します");
        }
        user.setUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("ユーザーが存在しません");
        }
        if (attendanceRepository.existsByUser_Id(id)) {
            throw new BusinessException("勤怠履歴があるユーザーは削除できません");
        }
        userRepository.deleteById(id);
    }

    private String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("ユーザー名は必須です");
        }
        String normalized = username.trim();
        if (normalized.length() > 30) {
            throw new BusinessException("ユーザー名は30文字以内にしてください");
        }
        return normalized;
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8 || rawPassword.length() > 64) {
            throw new BusinessException("パスワードは8〜64文字にしてください");
        }
    }

    private void validateRole(String role) {
        if (!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new BusinessException("ロールが不正です");
        }
    }
}
