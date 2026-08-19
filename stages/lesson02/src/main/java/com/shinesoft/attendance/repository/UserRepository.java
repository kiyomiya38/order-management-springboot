// Repositoryインターフェースを置くパッケージ
package com.shinesoft.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinesoft.attendance.domain.User;

// UserテーブルのDB操作窓口
// JpaRepository<エンティティ型, 主キー型>
public interface UserRepository extends JpaRepository<User, Long> {
    // ユーザー名で1件検索（存在しない場合があるのでOptional）
    Optional<User> findByUsername(String username);
}