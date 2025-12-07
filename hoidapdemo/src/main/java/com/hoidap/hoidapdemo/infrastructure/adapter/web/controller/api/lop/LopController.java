package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.api.lop;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.lop.CreateLopRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/classes")
@Tag(name = "Quản lý Lớp")
public class LopController {
    private final LopServicePort lopService;

    public LopController(LopServicePort lopService) {
        this.lopService = lopService;
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo lớp mới", description = "API này cho phép tạo lớp mới")
    public ResponseEntity<ApiResponse> createLop(@Valid @RequestBody CreateLopRequest request) {
        lopService.createLop(request);

        return ResponseEntity.ok(ApiResponse.builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo lớp thành công: " + request.getMaLop())
                .build());
    }
}
