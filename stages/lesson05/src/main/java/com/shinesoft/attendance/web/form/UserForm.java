package com.shinesoft.attendance.web.form; // フォームクラス用パッケージ

import jakarta.validation.constraints.NotBlank; // 必須チェックに使う
import jakarta.validation.constraints.Pattern; // 許可する値を制限する
import jakarta.validation.constraints.Size; // 文字数を制限する

public class UserForm {
    @NotBlank // 空文字・空白のみを禁止
    @Size(max = 30)
    private String username;

    @Size(max = 64)
    private String password; // 更新時は空欄許可にするためNotBlankを付けない

    @NotBlank // ロールは必須
    @Pattern(regexp = "ROLE_ADMIN|ROLE_USER")
    private String role;

    public String getUsername() { // username取得
        return username;
    }

    public String getPassword() { // password取得
        return password;
    }

    public String getRole() { // role取得
        return role;
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