package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {
    private String maLop;

    private String chuyenMon;
}
