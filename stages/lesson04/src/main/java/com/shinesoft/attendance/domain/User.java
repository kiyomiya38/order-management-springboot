// Entity（DBテーブル）を置くパッケージ
package com.shinesoft.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// このクラスをJPAの永続化対象（テーブル）として扱う
@Entity
// 対応するテーブル名
@Table(name = "users")
public class User {
    // 主キー
    @Id
    // DB側で自動採番（AUTO_INCREMENT）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NULL不可、重複不可、最大50文字
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // 以下はアクセサ（getter/setter）
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}