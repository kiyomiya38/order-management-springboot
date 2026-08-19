package com.shinesoft.attendance.config; // このクラスの所属パッケージ

import org.springframework.context.annotation.Bean; // Spring管理オブジェクト（Bean）を登録する
import org.springframework.context.annotation.Configuration; // 設定クラスであることを示す
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // セキュリティ設定を組み立てる
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Spring Securityを有効化
import org.springframework.security.core.userdetails.UserDetailsService; // ユーザー情報を取得する窓口
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCryptでパスワードをハッシュ化
import org.springframework.security.crypto.password.PasswordEncoder; // パスワードエンコーダの共通型
import org.springframework.security.web.SecurityFilterChain; // セキュリティの実行ルール本体

import com.shinesoft.attendance.domain.User; // DBから取得するアプリ側のユーザー
import com.shinesoft.attendance.repository.UserRepository; // DBからユーザーを取得するために使う

@Configuration // このクラスを設定クラスとしてSpringに登録
@EnableWebSecurity // Spring Securityの機能をONにする
public class SecurityConfig {

    @Bean // SecurityFilterChainをBeanとして登録
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth // URLごとのアクセス権ルール開始
                .requestMatchers("/login", "/styles.css").permitAll() // ログイン画面とCSSは未ログインでも許可
                .requestMatchers("/h2-console/**").permitAll() // 学習用にH2コンソールは許可
                .requestMatchers("/users/**").hasRole("ADMIN") // /users配下は管理者だけ許可
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin配下は管理者だけ許可
                .anyRequest().authenticated() // それ以外はログイン必須
            )
            .formLogin(form -> form // フォームログイン設定
                .loginPage("/login") // 自作ログイン画面のURL
                .defaultSuccessUrl("/", true) // ログイン成功後は常にトップへ
                .permitAll() // ログイン処理そのものは誰でも実行可能
            )
            .logout(logout -> logout // ログアウト設定
                .logoutUrl("/logout") // ログアウト実行URL
                .logoutSuccessUrl("/login?logout") // ログアウト後にログイン画面へ戻す
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2コンソールだけCSRF除外
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // H2画面表示のため同一オリジンiframe許可
        return http.build(); // 設定を確定して返す
    }

    @Bean // ユーザー認証で使うUserDetailsServiceを登録
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> { // ログイン時に入力されたユーザー名を受け取る
            User user = userRepository.findByUsername(username) // DBからユーザー検索
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "User not found: " + username)); // 見つからない場合は認証失敗
            return org.springframework.security.core.userdetails.User // Spring Security用ユーザーへ変換
                .withUsername(user.getUsername()) // 認証に使うユーザー名
                .password(user.getPassword()) // DB保存済みのハッシュ化パスワード
                .roles(user.getRole().replace("ROLE_", "")) // ROLE_ADMIN -> ADMIN に変換して設定
                .build(); // UserDetailsを作成して返す
        };
    }

    @Bean // パスワード照合方式をBeanとして登録
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt方式（平文保存しない）
    }
}