package com.shinesoft.attendance.domain; // ドメイン（Entity）層のパッケージ

import jakarta.persistence.*; // JPAアノテーション一式

@Entity // DBテーブルと対応するエンティティ
@Table(name = "users") // テーブル名を users に指定
public class User {
    @Id // 主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DBの自動採番を利用
    private Long id;

    @Column(nullable = false, unique = true) // 必須 + 重複不可（ログインID）
    private String username;

    @Column(nullable = false) // 必須（ハッシュ化パスワード）
    private String password;

    @Column(nullable = false) // 必須（ROLE_ADMIN / ROLE_USER など）
    private String role;

    public Long getId() { // id取得
        return id;
    }

    public String getUsername() { // username取得
        return username;
    }

    public String getPassword() { // password取得
        return password;
    }

    public String getRole() { // role取得
        return role;
    }

    public void setId(Long id) { // id設定
        this.id = id;
    }

    public void setUsername(String username) { // username設定
        this.username = username;
    }

    public void setPassword(String password) { // password設定
        this.password = password;
    }

    public void setRole(String role) { // role設定
        this.role = role;
    }
}