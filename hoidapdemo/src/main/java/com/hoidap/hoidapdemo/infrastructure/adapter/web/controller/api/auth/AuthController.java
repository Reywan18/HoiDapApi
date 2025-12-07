package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.api.auth;

import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.AuthResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.LoginRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.RegisterRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.user.ProfileUpdateRequest;
import org.springframework.http.ResponseEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Quản lý Đăng ký, Đăng nhập")
public class AuthController {
    private final UserServicePort userService;

    public AuthController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        String userId = userService.register(
                request.getEmail(),
                request.getPassword(),
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
    @Operation(summary = "Đăng nhập")
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

    @PostMapping("/profile/update")
    @Operation(summary = "Cập nhật thông tin")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.updateProfile(email, request.getMaLop(), request.getChuyenMon());

            return ResponseEntity.ok(AuthResponse.builder()
                    .status(AppStatus.SUCCESS.getCode())
                    .message(AppStatus.SUCCESS.getMessage())
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .status(AppStatus.MISSING_VALUE.getCode())
                    .message(AppStatus.MISSING_VALUE.getMessage() + " " + e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .status(AppStatus.INTERNAL_ERROR.getCode())
                    .message(AppStatus.INTERNAL_ERROR.getMessage())
                    .build());
        }
    }
}
