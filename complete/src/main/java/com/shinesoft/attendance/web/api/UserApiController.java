package com.shinesoft.attendance.web.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shinesoft.attendance.domain.User;
import com.shinesoft.attendance.service.UserService;
import com.shinesoft.attendance.web.api.dto.UserCreateRequest;
import com.shinesoft.attendance.web.api.dto.UserResponse;
import com.shinesoft.attendance.web.api.dto.UserUpdateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable("id") Long id) {
        return toResponse(userService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        return toResponse(userService.create(request.username(), request.password(), request.role()));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable("id") Long id,
                               @Valid @RequestBody UserUpdateRequest request) {
        return toResponse(userService.update(
            id, request.username(), request.password(), request.role()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
