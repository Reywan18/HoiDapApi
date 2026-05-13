package com.hoidap.hoidapdemo.dto.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDetailDto {
    private Long id;
    private Long conversationId;
    private String tieuDeConversation;

    // Người báo cáo (CVHT - suy ra từ conversation.ma_cv)
    private String maCv;
    private String tenCv;

    // Người bị báo cáo (SV - suy ra từ conversation.ma_sv)
    private String maSv;
    private String tenSv;

    private String lyDo;
    private LocalDateTime thoiGianBaoCao;
    private String trangThai;
}
