package com.hoidap.hoidapdemo.controller.api.chat;

import com.hoidap.hoidapdemo.dto.chat.ChatMessageDto;
import com.hoidap.hoidapdemo.dto.chat.MessageResponseDto;
import com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity;
import com.hoidap.hoidapdemo.entity.chat.MessageJpaEntity;
import com.hoidap.hoidapdemo.entity.enums.SenderType;
import com.hoidap.hoidapdemo.repository.chat.ConversationJpaRepository;
import com.hoidap.hoidapdemo.repository.chat.MessageJpaRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageJpaRepository messageRepo;
    private final ConversationJpaRepository conversationRepo;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          MessageJpaRepository messageRepo,
                          ConversationJpaRepository conversationRepo) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepo = messageRepo;
        this.conversationRepo = conversationRepo;
    }

    /**
     * Lắng nghe tín hiệu từ Frontend gửi qua STOMP tại đường dẫn: /app/chat.sendMessage
     * Sau khi xử lý lưu DB xong, sẽ đẩy ngược kết quả lên /topic/conversation/{conversationId}
     */
    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(@Payload ChatMessageDto chatMessage) {
        // 1. Lấy phòng chat từ DB
        ConversationJpaEntity conversation = conversationRepo.findById(chatMessage.getConversationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));

        // 2. Cập nhật thời gian update cuối cùng của phòng chat
        conversation.setNgayCapNhatCuoi(LocalDateTime.now());
        conversationRepo.save(conversation);

        // 3. Khởi tạo đối tượng tin nhắn mới để lưu vào DB
        MessageJpaEntity messageEntity = new MessageJpaEntity();
        messageEntity.setConversation(conversation);
        messageEntity.setNoiDung(chatMessage.getContent());
        messageEntity.setThoiGianGui(LocalDateTime.now());
        messageEntity.setNguoiGuiId(chatMessage.getSenderId());
        
        try {
            messageEntity.setNguoiGuiType(SenderType.valueOf(chatMessage.getSenderType()));
        } catch (Exception e) {
            messageEntity.setNguoiGuiType(SenderType.SINH_VIEN); // Fallback an toàn
        }

        // Lưu vào Database
        MessageJpaEntity savedMessage = messageRepo.save(messageEntity);

        // 4. Map sang DTO an toàn để bắn về Frontend
        MessageResponseDto responseDto = new MessageResponseDto();
        responseDto.setId(savedMessage.getId());
        responseDto.setConversationId(conversation.getId());
        responseDto.setNguoiGuiType(savedMessage.getNguoiGuiType().name());
        responseDto.setNguoiGuiId(savedMessage.getNguoiGuiId());
        responseDto.setNoiDung(savedMessage.getNoiDung());
        responseDto.setThoiGianGui(savedMessage.getThoiGianGui());
        responseDto.setFileName(savedMessage.getFileName());
        responseDto.setFileType(savedMessage.getFileType());

        // 5. Bắn Broadcast gói tin DTO này đến tất cả user đang mở kênh /topic/conversation/{id}
        String destination = "/topic/conversation/" + chatMessage.getConversationId();
        messagingTemplate.convertAndSend(destination, responseDto);
    }
}
