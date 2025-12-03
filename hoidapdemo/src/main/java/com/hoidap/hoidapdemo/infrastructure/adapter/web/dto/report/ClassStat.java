package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassStat {
    private String maLop;
    private String chuyenNganh;
    private Long questionCount;
}
