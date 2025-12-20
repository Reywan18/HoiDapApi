package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String maDinhDanh;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String role;

    private String maLop;
    private String tenLop;

    private String maCoVan;
    private String tenCoVan;
}