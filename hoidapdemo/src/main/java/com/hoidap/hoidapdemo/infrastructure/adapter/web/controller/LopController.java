package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.AuthResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.CreateLopRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/classes")
public class LopController {
    private final LopServicePort lopService;

    public LopController(LopServicePort lopService) {
        this.lopService = lopService;
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> createLop(@Valid @RequestBody CreateLopRequest request) {
        lopService.createLop(request);

        return ResponseEntity.ok(AuthResponse.builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo lớp thành công: " + request.getMaLop())
                .build());
    }
}
