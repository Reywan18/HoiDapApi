package com.hoidap.hoidapdemo.controller.api.user;

import com.hoidap.hoidapdemo.service.UserServiceImpl;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.dto.user.UserProfileResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Quản Lý Người Dùng", description = "Các API liên quan đến thông tin cá nhân")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Xem thông tin cá nhân", description = "Dựa vào Token đăng nhập để trả về thông tin chi tiết của Sinh viên hoặc CVHT")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication authentication) {
        String email = authentication.getName();

        UserProfileResponse profile = userService.getMyProfile(email);

        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy thông tin thành công")
                .data(profile)
                .build());
    }
}