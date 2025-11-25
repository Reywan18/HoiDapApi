package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller;

import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.UserDto;
import org.springframework.http.ResponseEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.AuthResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.LoginRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.RegisterRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserServicePort userService;

    public AuthController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        String userId = userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getMaDinhDanh(),
                request.getHoTen(),
                request.getSoDienThoai(),
                request.getRole()
        );

        AuthResponse response = AuthResponse.builder()
                .status(AppStatus.SUCCESS.getCode())
                .message(AppStatus.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getEmail(), request.getPassword());

            AuthResponse response = AuthResponse.builder()
                    .status(AppStatus.SUCCESS.getCode())
                    .message("Đăng nhập thành công")
                    .token(token)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    AuthResponse.builder()
                            .status(401)
                            .message("Đăng nhập thất bại: " + e.getMessage())
                            .build()
            );
        }
    }
}
