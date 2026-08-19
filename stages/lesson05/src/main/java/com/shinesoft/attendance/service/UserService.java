package com.shinesoft.attendance.service; // Service層のパッケージ

import java.util.List; // 一覧取得で使う

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードハッシュ化に使う
import org.springframework.stereotype.Service; // Serviceクラスとして登録
import org.springframework.transaction.annotation.Transactional; // 更新処理をトランザクション化

import com.shinesoft.attendance.domain.User; // Userエンティティ
import com.shinesoft.attendance.exception.BusinessException; // 業務例外
import com.shinesoft.attendance.repository.AttendanceRepository; // 勤怠参照チェック
import com.shinesoft.attendance.repository.UserRepository; // UserのDBアクセス

@Service // Spring管理対象（業務ロジック層）
public class UserService {
    private final UserRepository userRepository; // User保存/検索に使う
    private final AttendanceRepository attendanceRepository; // 削除前の勤怠参照確認
    private final PasswordEncoder passwordEncoder; // パスワード暗号化に使う

    public UserService(UserRepository userRepository,
                       AttendanceRepository attendanceRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository; // 依存注入
        this.attendanceRepository = attendanceRepository; // 依存注入
        this.passwordEncoder = passwordEncoder; // 依存注入
    }

    public User getByUsername(String username) { // ユーザー名で1件取得
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません")); // 見つからなければ業務エラー
    }

    public List<User> list() { // 一覧取得
        return userRepository.findAll();
    }

    public User get(Long id) { // IDで1件取得
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("ユーザーが存在しません")); // 見つからなければ業務エラー
    }

    @Transactional // 作成処理を1トランザクションで実行
    public User create(String username, String rawPassword, String role) {
        username = validateUsername(username);
        validatePassword(rawPassword);
        validateRole(role);
        if (userRepository.findByUsername(username).isPresent()) { // ユーザー名重複チェック
            throw new BusinessException("ユーザー名が既に存在します");
        }
        User user = new User(); // 新規エンティティ生成
        user.setUsername(username); // ユーザー名設定
        user.setPassword(passwordEncoder.encode(rawPassword)); // パスワードをハッシュ化して設定
        user.setRole(role); // ロール設定
        return userRepository.save(user); // 保存して返す
    }

    @Transactional // 更新処理を1トランザクションで実行
    public User update(Long id, String username, String rawPassword, String role) {
        username = validateUsername(username);
        if (rawPassword != null && !rawPassword.isBlank()) {
            validatePassword(rawPassword);
        }
        validateRole(role);
        User user = get(id); // 更新対象を取得
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) { // 他人と重複しないか確認
            throw new BusinessException("ユーザー名が既に存在します");
        }
        user.setUsername(username); // ユーザー名更新
        if (rawPassword != null && !rawPassword.isBlank()) { // パスワード入力がある場合のみ更新
            user.setPassword(passwordEncoder.encode(rawPassword)); // ハッシュ化して更新
        }
        user.setRole(role); // ロール更新
        return userRepository.save(user); // 保存して返す
    }

    @Transactional // 削除処理を1トランザクションで実行
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("ユーザーが存在しません");
        }
        if (attendanceRepository.existsByUser_Id(id)) {
            throw new BusinessException("勤怠履歴があるユーザーは削除できません");
        }
        userRepository.deleteById(id); // 参照が無い場合だけ削除
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