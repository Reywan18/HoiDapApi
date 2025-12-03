package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.answer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LatestAnswerResponse {
    private String noiDung;
    private String tenCvht;
    private LocalDateTime ngayTraLoi;
    private String fileName;
    private String fileDownloadUrl;
}
