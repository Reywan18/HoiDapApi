package com.hoidap.hoidapdemo.controller.api.admin;

import com.hoidap.hoidapdemo.service.port.LopServicePort;
import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.dto.lop.CreateLopRequest;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/classes")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminLopApiController {

    private final LopServicePort lopService;

    public AdminLopApiController(LopServicePort lopService) {
        this.lopService = lopService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<LopJpaEntity>>> getAllClasses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Page<LopJpaEntity> result = lopService.getAllLop(keyword, org.springframework.data.domain.PageRequest.of(page, size));
        
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<LopJpaEntity>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(result)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createClass(@RequestBody CreateLopRequest request) {
        lopService.createLop(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo lớp thành công!")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateClass(@PathVariable String id, @RequestBody CreateLopRequest request) {
        lopService.updateLop(id, request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Cập nhật lớp thành công!")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteClass(@PathVariable String id) {
        lopService.deleteLop(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Xóa lớp thành công!")
                .build());
    }

    @GetMapping("/export")
    public ResponseEntity<org.springframework.core.io.Resource> exportExcel() {
        String filename = "danh_sach_lop.xlsx";
        List<LopJpaEntity> list = lopService.getAllLop(null, org.springframework.data.domain.Pageable.unpaged()).getContent();
        org.springframework.core.io.InputStreamResource file = new org.springframework.core.io.InputStreamResource(com.hoidap.hoidapdemo.utils.excel.ExcelHelper.lopsToExcel(list));

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (com.hoidap.hoidapdemo.utils.excel.ExcelHelper.hasExcelFormat(file)) {
            try {
                List<LopJpaEntity> listLop = com.hoidap.hoidapdemo.utils.excel.ExcelHelper.excelToLops(file.getInputStream());
                lopService.saveListLop(listLop);
                return ResponseEntity.ok(ApiResponse.<String>builder()
                        .status(AppStatus.SUCCESS.getCode())
                        .message("Import danh sách lớp thành công!")
                        .build());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                        .status(AppStatus.INTERNAL_ERROR.getCode())
                        .message("Không thể upload file: " + e.getMessage())
                        .build());
            }
        }
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .status(AppStatus.INVALID_REQUEST_DATA.getCode())
                .message("Vui lòng chọn file Excel chuẩn (.xlsx)!")
                .build());
    }
}
