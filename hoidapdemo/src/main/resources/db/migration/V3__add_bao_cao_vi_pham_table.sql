-- Migration V3: Tạo bảng lưu báo cáo vi phạm từ CVHT
CREATE TABLE IF NOT EXISTS bao_cao_vi_pham (
    id BIGINT NOT NULL AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    nguoi_bao_cao_id VARCHAR(20),
    ly_do VARCHAR(500),
    thoi_gian_bao_cao DATETIME(6),
    trang_thai VARCHAR(20) DEFAULT 'PENDING',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
