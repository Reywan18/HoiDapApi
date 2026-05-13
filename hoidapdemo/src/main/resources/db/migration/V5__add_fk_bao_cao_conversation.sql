-- Migration V5: Thêm khóa ngoại thực thụ cho bảng bao_cao_vi_pham liên kết với bảng conversation
ALTER TABLE bao_cao_vi_pham
ADD CONSTRAINT fk_bao_cao_conversation
FOREIGN KEY (conversation_id) REFERENCES conversation(id)
ON DELETE CASCADE;
