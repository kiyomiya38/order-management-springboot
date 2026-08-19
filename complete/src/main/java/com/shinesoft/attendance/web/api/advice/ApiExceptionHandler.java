package com.shinesoft.attendance.web.api.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.shinesoft.attendance.exception.BusinessException;
import com.shinesoft.attendance.web.api.dto.ErrorResponse;

@RestControllerAdvice(basePackages = "com.shinesoft.attendance.web.api")
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBusiness(BusinessException ex) {
        return new ErrorResponse("BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("入力値が不正です");
        return new ErrorResponse("VALIDATION_ERROR", message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorResponse("VALIDATION_ERROR", ex.getName() + ": 入力値が不正です");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return new ErrorResponse("VALIDATION_ERROR", "JSONの形式が不正です");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return new ErrorResponse(
            "UNSUPPORTED_MEDIA_TYPE",
            "Content-Typeはapplication/jsonを指定してください");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleUnsupportedMethod(HttpRequestMethodNotSupportedException ex) {
        return new ErrorResponse("METHOD_NOT_ALLOWED", "HTTPメソッドが許可されていません");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NoResourceFoundException ex) {
        return new ErrorResponse("NOT_FOUND", "APIが存在しません");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknown(Exception ex) {
        log.error("Unexpected API error", ex);
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "予期しないエラーが発生しました");
    }
}
