package com.shinesoft.attendance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword("password");
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }
    }
}
