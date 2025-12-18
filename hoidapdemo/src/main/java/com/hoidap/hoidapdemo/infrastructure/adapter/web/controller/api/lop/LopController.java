package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.api.lop;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/classes")
@Tag(name = "Quản lý Lớp")
public class LopController {
    private final LopServicePort lopService;

    public LopController(LopServicePort lopService) {
        this.lopService = lopService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả lớp học", description = "Trả về Mã lớp và Tên hiển thị (Ngành)")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getAllLops() {

        List<LopJpaEntity> entities = lopService.getAllLop();

        List<Map<String, String>> data = entities.stream().map(lop -> {
            Map<String, String> item = new HashMap<>();
            item.put("maLop", lop.getMaLop());
            String tenHienThi = lop.getMaLop();
            if (lop.getChuyenNganh() != null) {
                tenHienThi += " - " + lop.getChuyenNganh();
            }
            item.put("tenHienThi", tenHienThi);
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<Map<String, String>>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách lớp thành công")
                .data(data)
                .build());
    }
}
