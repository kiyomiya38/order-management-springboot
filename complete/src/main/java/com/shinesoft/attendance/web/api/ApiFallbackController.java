package com.shinesoft.attendance.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shinesoft.attendance.web.api.dto.ErrorResponse;

@RestController
@RequestMapping("/api")
public class ApiFallbackController {

    @RequestMapping({
        "/users",
        "/users/{id}",
        "/attendances/clock-in",
        "/attendances/clock-out"
    })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse methodNotAllowed() {
        return new ErrorResponse(
            "METHOD_NOT_ALLOWED",
            "HTTPメソッドが許可されていません");
    }

    @RequestMapping("/**")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound() {
        return new ErrorResponse("NOT_FOUND", "APIが存在しません");
    }
}
