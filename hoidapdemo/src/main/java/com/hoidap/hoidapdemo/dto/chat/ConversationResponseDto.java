package com.hoidap.hoidapdemo.dto.chat;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationResponseDto {
    private Long id;
    private String tieuDe;
    private String trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhatCuoi;
    private String maSv;
    private String tenSv;
    private String emailSv;
    private String sdtSv;
    private String maLopSv;
    private String khoaSv;

    private String maCv;
    private String tenCv;
    private String emailCv;
    private String sdtCv;
    private String chuyenMonCv;
}
