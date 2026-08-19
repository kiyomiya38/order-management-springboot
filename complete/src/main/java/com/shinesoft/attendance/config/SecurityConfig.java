package com.shinesoft.attendance.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinesoft.attendance.repository.UserRepository;
import com.shinesoft.attendance.web.api.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint apiAuthenticationEntryPoint =
            (request, response, exception) ->
                writeApiError(response, objectMapper, HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED", "認証が必要です");

        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/attendances/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(basic ->
                basic.authenticationEntryPoint(apiAuthenticationEntryPoint))
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .requestCache(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(apiAuthenticationEntryPoint)
                .accessDeniedHandler((request, response, exception) ->
                    writeApiError(response, objectMapper, HttpStatus.FORBIDDEN,
                        "FORBIDDEN", "この操作を行う権限がありません"))
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/styles.css", "/users.js", "/favicon.ico")
                    .permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/users/**", "/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    private static void writeApiError(HttpServletResponse response,
                                      ObjectMapper objectMapper,
                                      HttpStatus status,
                                      String code,
                                      String message) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (status == HttpStatus.UNAUTHORIZED) {
            response.setHeader(
                HttpHeaders.WWW_AUTHENTICATE,
                "Basic realm=\"attendance-api\"");
        }
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(code, message));
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "User not found: " + username));
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
