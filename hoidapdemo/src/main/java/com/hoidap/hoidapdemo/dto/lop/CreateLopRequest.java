package com.hoidap.hoidapdemo.dto.lop;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLopRequest {
    @NotBlank(message = "Mã lớp không được để trống")
    private String maLop;

    private String khoaHoc;
    private String chuyenNganh;

    private String maCvht;
}
