package com.hoidap.hoidapdemo.repository.chat;

import com.hoidap.hoidapdemo.entity.chat.MessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {
    
    // Lấy toàn bộ lịch sử tin nhắn trong 1 phòng chat, xếp theo thời gian gửi (cũ nhất -> mới nhất)
    List<MessageJpaEntity> findByConversation_IdOrderByThoiGianGuiAsc(Long conversationId);
}
