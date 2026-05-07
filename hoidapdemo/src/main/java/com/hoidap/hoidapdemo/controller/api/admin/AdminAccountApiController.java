package com.hoidap.hoidapdemo.controller.api.admin;

import com.hoidap.hoidapdemo.service.AdminServiceImpl;
import com.hoidap.hoidapdemo.dto.auth.AccountCreatedResponse;
import com.hoidap.hoidapdemo.dto.auth.CreateUserRequest;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminAccountApiController {

    private final AdminServiceImpl adminService;

    public AdminAccountApiController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/student")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> createStudentAccount(@RequestBody CreateUserRequest request) {
        AccountCreatedResponse response = adminService.createStudentAccount(request);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo tài khoản sinh viên thành công")
                .data(response)
                .build());
    }

    @PostMapping("/cvht")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> createAdvisorAccount(@RequestBody CreateUserRequest request) {
        AccountCreatedResponse response = adminService.createAdvisorAccount(request);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo tài khoản CVHT thành công")
                .data(response)
                .build());
    }

    @PostMapping("/student/{id}/reset-password")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> resetStudentPassword(@PathVariable String id) {
        AccountCreatedResponse response = adminService.resetStudentPassword(id);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đặt lại mật khẩu sinh viên thành công")
                .data(response)
                .build());
    }

    @PostMapping("/cvht/{id}/reset-password")
    public ResponseEntity<ApiResponse<AccountCreatedResponse>> resetAdvisorPassword(@PathVariable String id) {
        AccountCreatedResponse response = adminService.resetAdvisorPassword(id);
        return ResponseEntity.ok(ApiResponse.<AccountCreatedResponse>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đặt lại mật khẩu CVHT thành công")
                .data(response)
                .build());
    }
}
