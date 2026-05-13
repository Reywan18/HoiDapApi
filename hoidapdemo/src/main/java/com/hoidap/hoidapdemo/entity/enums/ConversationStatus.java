package com.hoidap.hoidapdemo.entity.enums;

public enum ConversationStatus {
    CHATTING_WITH_BOT,   // Đang chat với trợ lý ảo
    WAITING_FOR_CVHT,    // Đang đợi CVHT tiếp nhận
    CHATTING_WITH_CVHT,  // Đang được CVHT hỗ trợ
    REPORTED,            // Đã bị báo cáo vi phạm
    RESOLVED             // Đã giải quyết / Đóng
}
