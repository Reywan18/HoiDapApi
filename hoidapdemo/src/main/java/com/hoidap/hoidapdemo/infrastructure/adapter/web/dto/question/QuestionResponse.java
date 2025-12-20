package com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuestionResponse {
    private Long maCauHoi;
    private String tieuDe;
    private String noiDung;
    private String trangThai;
    private LocalDateTime ngayGui;
    private LocalDateTime ngayCapNhatCuoi;

    private String maSinhVien;
    private String tenSinhVien;
    private String maCvht;
    private String tenCvht;

    private String fileDownloadUri;
    private String fileName;

    private String maLop;
    private String chuyenNganh;
    private String khoaHoc;
}
