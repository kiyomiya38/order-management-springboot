package com.shinesoft.attendance.web.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(max = 30) String username,
        @Size(min = 8, max = 64) String password,
        @NotBlank @Pattern(regexp = "ROLE_ADMIN|ROLE_USER") String role) {
}
