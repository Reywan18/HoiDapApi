package com.hoidap.hoidapdemo.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassStat {
    private String maLop;
    private String chuyenNganh;
    private Long questionCount;
}
