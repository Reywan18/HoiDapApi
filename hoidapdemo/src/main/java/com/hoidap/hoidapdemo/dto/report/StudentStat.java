package com.hoidap.hoidapdemo.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStat {
    private String name;
    private String maSv;
    private Long questionCount;
}
