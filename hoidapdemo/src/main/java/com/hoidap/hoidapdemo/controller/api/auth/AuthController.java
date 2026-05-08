package com.hoidap.hoidapdemo.controller.api.auth;

import com.hoidap.hoidapdemo.service.port.UserServicePort;
import com.hoidap.hoidapdemo.dto.auth.AuthResponse;
import com.hoidap.hoidapdemo.dto.auth.LoginRequest;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.dto.user.ProfileUpdateRequest;
import com.hoidap.hoidapdemo.dto.user.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Quản lý Đăng ký, Đăng nhập")
public class AuthController {
    // dependencies
    private final UserServicePort userService;

    public AuthController(UserServicePort userService) {
        this.userService = userService;
    }

    // @PostMapping("/register")
    // @Operation(summary = "Đăng ký")
    // public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody
    // RegisterRequest request) {
    // String userId = userService.register(
    // request.getEmail(),
    // request.getPassword(),
    // request.getHoTen(),
    // request.getSoDienThoai(),
    // request.getRole()
    // );
    //
    // AuthResponse response = AuthResponse.builder()
    // .status(AppStatus.SUCCESS.getCode())
    // .message(AppStatus.SUCCESS.getMessage())
    // .build();
    // return ResponseEntity.ok(response);
    // }

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

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body(
                    AuthResponse.builder()
                            .status(401)
                            .message("Tài khoản hoặc mật khẩu không chính xác.")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    AuthResponse.builder()
                            .status(401)
                            .message("Đăng nhập thất bại: Lỗi hệ thống.")
                            .build());
        }
    }

    @PostMapping("/profile/update")
    @Operation(summary = "Cập nhật thông tin")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.updateProfile(email, request);

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

    @PostMapping("/password/change")
    @Operation(summary = "Đổi mật khẩu")
    public ResponseEntity<AuthResponse> changePassword(
            @Valid @RequestBody com.hoidap.hoidapdemo.dto.auth.ChangePasswordRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(AuthResponse.builder()
                    .status(AppStatus.SUCCESS.getCode())
                    .message("Đổi mật khẩu thành công")
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .status(AppStatus.MISSING_VALUE.getCode())
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .status(AppStatus.INTERNAL_ERROR.getCode())
                    .message(AppStatus.INTERNAL_ERROR.getMessage())
                    .build());
        }
    }

    /**
     * API lấy thông tin Profile cơ bản (Dành cho Header/Sidebar của Frontend).
     * Bao gồm cả thông tin Cố vấn học tập (CVHT) của sinh viên đó để hiển thị.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        try {
            UserProfileResponse profile = userService.getMyProfile(currentEmail);
            return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                    .status(AppStatus.SUCCESS.getCode())
                    .message("Lấy thông tin thành công")
                    .data(profile)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.<UserProfileResponse>builder()
                    .status(404)
                    .message(e.getMessage())
                    .build());
        }
    }
}
