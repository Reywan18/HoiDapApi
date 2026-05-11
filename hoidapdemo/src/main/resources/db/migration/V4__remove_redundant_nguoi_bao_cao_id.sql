-- Migration V4: Xóa cột nguoi_bao_cao_id khỏi bảng bao_cao_vi_pham
-- (Redundant: đã có thể suy ra từ conversation_id -> conversation.ma_cv)
ALTER TABLE bao_cao_vi_pham DROP COLUMN nguoi_bao_cao_id;
