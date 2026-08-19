package com.shinesoft.attendance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String userPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.admin-password:}") String adminPassword,
                      @Value("${app.seed.user-password:}") String userPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) {
        if (adminPassword.isBlank() || userPassword.isBlank()) {
            throw new IllegalStateException("初期ユーザー投入を有効にする場合は初期パスワードが必要です");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user1").isEmpty()) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword(passwordEncoder.encode(userPassword));
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }
    }
}
