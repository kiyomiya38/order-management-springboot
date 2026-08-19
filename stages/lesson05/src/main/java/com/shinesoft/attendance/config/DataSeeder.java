package com.shinesoft.attendance.config; // 設定クラス群のパッケージ

import org.springframework.boot.CommandLineRunner; // 起動直後に処理を実行するためのIF
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; // 設定で投入可否を切り替える
import org.springframework.beans.factory.annotation.Value; // 設定値を受け取る
import org.springframework.stereotype.Component; // Spring管理対象として登録

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.repository.UserRepository; // User保存/検索に使うRepository
import org.springframework.security.crypto.password.PasswordEncoder; // パスワードをハッシュ化するために使う

@Component // アプリ起動時に読み込まれるBean
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository; // ユーザー永続化用
    private final PasswordEncoder passwordEncoder; // 平文パスワードをそのまま保存しないための依存
    private final String adminPassword;
    private final String userPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.admin-password:}") String adminPassword,
                      @Value("${app.seed.user-password:}") String userPassword) {
        this.userRepository = userRepository; // 依存注入
        this.passwordEncoder = passwordEncoder; // 依存注入
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) { // アプリ起動後に1回実行される
        if (adminPassword.isBlank() || userPassword.isBlank()) {
            throw new IllegalStateException("初期ユーザー投入を有効にする場合は初期パスワードが必要です");
        }

        if (userRepository.findByUsername("admin").isEmpty()) { // adminがいない時だけ作成
            User admin = new User(); // 管理者アカウント作成
            admin.setUsername("admin"); // ログインID
            admin.setPassword(passwordEncoder.encode(adminPassword)); // 設定値をハッシュ化して保存
            admin.setRole("ROLE_ADMIN"); // 管理者ロール
            userRepository.save(admin); // DBへ保存
        }

        if (userRepository.findByUsername("user1").isEmpty()) { // user1がいない時だけ作成
            User user = new User(); // 一般ユーザー作成
            user.setUsername("user1"); // ログインID
            user.setPassword(passwordEncoder.encode(userPassword)); // 設定値をハッシュ化して保存
            user.setRole("ROLE_USER"); // 一般ユーザーロール
            userRepository.save(user); // DBへ保存
        }
    }
}