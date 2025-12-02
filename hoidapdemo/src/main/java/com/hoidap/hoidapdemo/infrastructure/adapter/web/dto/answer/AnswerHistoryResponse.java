package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.answer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnswerHistoryResponse {
    private int version;
    private String noiDung;
    private LocalDateTime thoiGianTao;
    private String fileName;
    private String downloadUrl;
}
