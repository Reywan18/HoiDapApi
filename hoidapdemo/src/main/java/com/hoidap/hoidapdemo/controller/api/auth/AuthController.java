package com.hoidap.hoidapdemo.controller.api.auth;

import com.hoidap.hoidapdemo.service.port.UserServicePort;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import java.util.Optional;
import com.hoidap.hoidapdemo.dto.auth.AuthResponse;
import com.hoidap.hoidapdemo.dto.auth.LoginRequest;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.dto.user.ProfileUpdateRequest;
import org.springframework.http.ResponseEntity;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Quản lý Đăng ký, Đăng nhập")
public class AuthController {
    // dependencies
    private final UserServicePort userService;
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;

    public AuthController(UserServicePort userService, SinhVienJpaRepository sinhVienRepo, CVHTJpaRepository cvhtRepo) {
        this.userService = userService;
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
    }

//    @PostMapping("/register")
//    @Operation(summary = "Đăng ký")
//    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
//        String userId = userService.register(
//                request.getEmail(),
//                request.getPassword(),
//                request.getHoTen(),
//                request.getSoDienThoai(),
//                request.getRole()
//        );
//
//        AuthResponse response = AuthResponse.builder()
//                .status(AppStatus.SUCCESS.getCode())
//                .message(AppStatus.SUCCESS.getMessage())
//                .build();
//        return ResponseEntity.ok(response);
//    }

    /**
     * API Đăng nhập.
     * @param request: Chứa Email và Password người dùng nhập.
     * Logic:
     * 1. Gọi Service kiểm tra email/pass.
     * 2. Nếu đúng -> Sinh ra chuỗi JWT Token.
     * 3. Trả Token về cho Frontend lưu (thường lưu ở LocalStorage).
     */
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

    /**
     * API Cập nhật thông tin cá nhân (SĐT, Họ tên...).
     * @param authentication: Lấy từ Security Context để biết ai đang gọi API này (không cho sửa hộ người khác).
     */
    @PostMapping("/profile/update")
    @Operation(summary = "Cập nhật thông tin")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request, Authentication authentication) {
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

    /**
     * API lấy thông tin Profile cơ bản (Dành cho Header/Sidebar của Frontend).
     * Bao gồm cả thông tin Cố vấn học tập (CVHT) của sinh viên đó để hiển thị.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        Map<String, Object> data = new HashMap<>();

        Optional<SinhVienJpaEntity> svOpt = sinhVienRepo.findByEmail(currentEmail);
        
        if (svOpt.isPresent()) {
            SinhVienJpaEntity sv = svOpt.get();
            data.put("maSv", sv.getMaSv());
            data.put("maDinhDanh", sv.getMaSv());
            data.put("hoTen", sv.getHoTen());
            data.put("email", sv.getEmail());
            data.put("soDienThoai", sv.getSoDienThoai());
            data.put("role", "SINH_VIEN");

            if (sv.getLop() != null) {
                data.put("maLop", sv.getLop().getMaLop());
                data.put("chuyenMon", sv.getLop().getChuyenNganh());
                if (sv.getLop().getCvht() != null) {
                    data.put("cvhtMa", sv.getLop().getCvht().getMaCv());
                    data.put("cvhtHoTen", sv.getLop().getCvht().getHoTen());
                }
            }
        } else {
            Optional<CVHTJpaEntity> cvhtOpt = cvhtRepo.findByEmail(currentEmail);
            if (cvhtOpt.isPresent()) {
                CVHTJpaEntity cvht = cvhtOpt.get();
                data.put("maCv", cvht.getMaCv());
                data.put("maDinhDanh", cvht.getMaCv());
                data.put("hoTen", cvht.getHoTen());
                data.put("email", cvht.getEmail());
                data.put("soDienThoai", cvht.getSoDienThoai());
                data.put("role", "CVHT");
            } else {
                throw new RuntimeException("Không tìm thấy account cho email: " + currentEmail);
            }
        }

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy thông tin thành công")
                .data(data)
                .build());
    }
}
