package com.hoidap.hoidapdemo.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long conversationId;
    private String senderId; // ma_sv hoặc ma_cv
    private String senderType; // "SINH_VIEN", "CVHT", "BOT"
    private String content; // Nội dung tin nhắn
}
