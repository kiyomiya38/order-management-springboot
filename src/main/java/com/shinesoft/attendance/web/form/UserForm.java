package com.shinesoft.attendance.web.form;

import jakarta.validation.constraints.NotBlank;

public class UserForm {
    @NotBlank
    private String username;

    private String password;

    @NotBlank
    private String role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
