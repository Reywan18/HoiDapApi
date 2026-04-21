package com.hoidap.hoidapdemo.controller.api.ai;

import com.hoidap.hoidapdemo.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    private final AiService aiService;

    public AiChatController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * API công khai (Public API) dành cho Sinh viên chat với AI
     */
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody java.util.Map<String, String> payload) {
        try {
            String message = payload.getOrDefault("message", payload.get("question"));
            if (message == null)
                message = payload.values().stream().findFirst().orElse("");

            System.out.println("\n\n=======================================================");
            System.out.println("ĐÃ NHẬN ĐƯỢC REQUEST CHAT: " + message);
            System.out.println("=======================================================\n");

            // Đẩy câu hỏi lên não bộ RAG
            String aiAnswer = aiService.chatWithAi(message);
            return ResponseEntity.ok(aiAnswer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Hệ thống AI đang bận hoặc gặp sự cố: " + e.getMessage());
        }
    }
}
