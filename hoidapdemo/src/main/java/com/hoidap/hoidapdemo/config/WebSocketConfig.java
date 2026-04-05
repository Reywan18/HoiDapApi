package com.hoidap.hoidapdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Bật một broker chặn các tin nhắn có tiền tố /topic hoặc /queue để gửi về cho client
        config.enableSimpleBroker("/topic", "/queue");
        
        // Tiền tố dùng khi client gửi tin nhắn lên server (ví dụ @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        
        // Tiền tố dành riêng cho việc gửi tin nhắn đến một người dùng cụ thể
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Khai báo url endpoint để frontend kết nối WebSocket tới
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép React kết nối tới
                .withSockJS(); // Cung cấp fallback dự phòng nếu mạng bị chặn WebSocket thuần
    }
}
