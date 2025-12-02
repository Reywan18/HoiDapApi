package com.hoidap.hoidapdemo.infrastructure.config;

import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleArgumentException(IllegalArgumentException e) {
        AppStatus status = AppStatus.USER_ALREADY_EXISTS;

        AuthResponse errorResponse = AuthResponse.builder()
                .status(status.getCode())
                .message(status.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();

        AuthResponse errorResponse = AuthResponse.builder()
                .status(AppStatus.INVALID_REQUEST_DATA.getCode())
                .message(errorMessage)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleGlobalException(Exception e) {
        AuthResponse errorResponse = AuthResponse.builder()
                .status(AppStatus.INTERNAL_ERROR.getCode())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
