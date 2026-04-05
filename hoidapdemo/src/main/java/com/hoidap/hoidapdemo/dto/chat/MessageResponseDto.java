package com.hoidap.hoidapdemo.dto.chat;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponseDto {
    private Long id;
    private Long conversationId;
    private String nguoiGuiType;
    private String nguoiGuiId;
    private String noiDung;
    private LocalDateTime thoiGianGui;
    private String fileName;
    private String fileType;
}
