package com.hoidap.hoidapdemo.controller.api.admin;

import com.hoidap.hoidapdemo.service.port.UserServicePort;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserApiController {

    private final UserServicePort userService;

    public AdminUserApiController(UserServicePort userService) {
        this.userService = userService;
    }

    // --- Sinh Viên ---
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<SinhVienJpaEntity>>> getAllStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Page<SinhVienJpaEntity> result = userService.getAllSinhVien(keyword, org.springframework.data.domain.PageRequest.of(page, size));
        
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<SinhVienJpaEntity>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(result)
                .build());
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<String>> updateStudent(@PathVariable String id, @RequestBody SinhVienJpaEntity sv) {
        sv.setMaSv(id); // Đảm bảo ID đúng
        userService.saveSinhVien(sv);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Cập nhật sinh viên thành công!")
                .build());
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable String id) {
        userService.deleteSinhVien(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã xóa sinh viên: " + id)
                .build());
    }

    // --- CVHT ---
    @GetMapping("/cvht")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CVHTJpaEntity>>> getAllAdvisors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        org.springframework.data.domain.Page<CVHTJpaEntity> result = userService.getAllCVHT(keyword, org.springframework.data.domain.PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<CVHTJpaEntity>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(result)
                .build());
    }

    @PutMapping("/cvht/{id}")
    public ResponseEntity<ApiResponse<String>> updateAdvisor(@PathVariable String id, @RequestBody CVHTJpaEntity cv) {
        cv.setMaCv(id); // Đảm bảo ID đúng
        userService.saveCVHT(cv);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Cập nhật CVHT thành công!")
                .build());
    }

    @DeleteMapping("/cvht/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAdvisor(@PathVariable String id) {
        userService.deleteCVHT(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã xóa CVHT: " + id)
                .build());
    }
}
