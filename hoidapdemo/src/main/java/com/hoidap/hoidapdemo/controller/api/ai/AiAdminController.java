package com.hoidap.hoidapdemo.controller.api.ai;

import com.hoidap.hoidapdemo.service.port.AiServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/ai")
public class AiAdminController {

    private final AiServicePort aiService;

    public AiAdminController(AiServicePort aiService) {
        this.aiService = aiService;
    }

    /**
     * API này dành riêng cho Admin (Phòng đào tạo)
     * Dùng để upload các file quy định mới (PDF). Khi upload lên, AI sẽ "đọc và
     * nhớ" ngay lập tức.
     */
    @PostMapping("/upload-pdf")
    public ResponseEntity<String> uploadRulesPdf(@RequestParam("file") MultipartFile file) {
        try {
            aiService.processAndSavePdf(file);
            return ResponseEntity.ok("Tuyệt vời! File PDF đã được nạp thành công vào hệ thống tri thức AI (ChromaDB).");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi xử lý file PDF: " + e.getMessage());
        }
    }

    /**
     * API này giúp Admin (hoặc bạn) kiểm tra thử xem trong bộ nhớ ChromaDB
     * AI có thực sự nhớ các đoạn liên quan đến từ khoá hay không.
     */
    @GetMapping("/test-chroma")
    public ResponseEntity<String> testChromaDbMemory(@RequestParam("question") String question) {
        try {
            String result = aiService.testSearchDb(question);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi truy vấn: " + e.getMessage());
        }
    }
}
