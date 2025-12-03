package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public  class AdvisorStat {
    private String name;
    private Double avgResponseTimeHours;
    private Long answeredCount;
}