package com.hoidap.hoidapdemo.controller.admin;

import com.hoidap.hoidapdemo.service.AdminServiceImpl;
import com.hoidap.hoidapdemo.utils.AppStatus;
import com.hoidap.hoidapdemo.dto.auth.AccountCreatedResponse;
import com.hoidap.hoidapdemo.dto.auth.CreateUserRequest;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/accounts")
@Tag(name = "Admin - Quản lý tài khoản")
public class AdminAccountController {
    private final AdminServiceImpl adminService;

    public AdminAccountController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/student")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tài khoản Sinh viên", description = "Chỉ cần nhập Email. Hệ thống tự sinh Password.")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> createStudent(@RequestBody @Valid CreateUserRequest request) {
        AccountCreatedResponse response = adminService.createStudentAccount(request);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(response)
                .build());
    }

    @PostMapping("/advisor")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tài khoản CVHT")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> createAdvisor(@RequestBody @Valid CreateUserRequest request) {
        AccountCreatedResponse response = adminService.createAdvisorAccount(request);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(response)
                .build());
    }
}
