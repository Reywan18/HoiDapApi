package com.hoidap.hoidapdemo.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {
    private String hoTen;
    private String soDienThoai;

    private String newPassword;

    private String maLop;
    private String chuyenMon;
}
