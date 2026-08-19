// 設定クラスを置くパッケージ
package com.shinesoft.attendance.config;

// アプリ起動時に1回だけ実行される処理を作るためのインターフェース
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.repository.UserRepository;

// Springの設定クラス
@Configuration
public class DataSeeder {

    // Beanとして登録され、起動時に自動実行される
    @Bean
    CommandLineRunner seedUser(UserRepository userRepository) {
        // argsは起動引数（今回は未使用）
        return args -> {
            // user1 がいない時だけ作成（重複投入を防ぐ）
            if (userRepository.findByUsername("user1").isEmpty()) {
                User user = new User();
                user.setUsername("user1");
                userRepository.save(user);
            }
        };
    }
}