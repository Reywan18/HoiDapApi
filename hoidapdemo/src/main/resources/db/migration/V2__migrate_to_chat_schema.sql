SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS phien_ban_tra_loi;
DROP TABLE IF EXISTS cau_tra_loi;
DROP TABLE IF EXISTS cau_hoi;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_sv VARCHAR(10) NOT NULL,
    ma_cv VARCHAR(10) NULL,
    tieu_de VARCHAR(255) NULL,
    trang_thai VARCHAR(50) NOT NULL, 
    ngay_tao DATETIME(6) NOT NULL,
    ngay_cap_nhat_cuoi DATETIME(6) NULL,
    CONSTRAINT fk_conv_sv FOREIGN KEY (ma_sv) REFERENCES sinh_vien(ma_sv),
    CONSTRAINT fk_conv_cv FOREIGN KEY (ma_cv) REFERENCES cvht(ma_cv)
);

CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    nguoi_gui_type VARCHAR(20) NOT NULL, 
    nguoi_gui_id VARCHAR(10) NULL,      
    noi_dung TEXT NULL,
    thoi_gian_gui DATETIME(6) NOT NULL,
    file_name VARCHAR(255) NULL,
    file_type VARCHAR(255) NULL,
    file_data LONGBLOB NULL,
    CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES conversation(id) ON DELETE CASCADE
);
