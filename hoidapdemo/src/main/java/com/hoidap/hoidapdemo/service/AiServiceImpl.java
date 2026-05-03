package com.hoidap.hoidapdemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import com.hoidap.hoidapdemo.service.port.AiServicePort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AiServiceImpl implements AiServicePort {

    private final VectorStore vectorStore;

    @Value("${app.gemini.api-key}")
    private String geminiApiKey;

    @Value("${app.gemini.url}")
    private String geminiApiUrl;

    public AiServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processAndSavePdf(MultipartFile file) throws IOException {
        // 1. Chuyển đổi file tải lên thành định dạng Resource
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
            }
        };

        // Sử dụng PagePdfDocumentReader (Chuyên dụng cho tệp PDF)
        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(resource);

        // 2. Trích xuất toàn bộ văn bản (text) từ file PDF
        List<Document> rawDocuments = documentReader.get();

        // --- BƯỚC LÀM SẠCH DỮ LIỆU (CLEAN-UP) ---
        // Do PagePdfDocumentReader thường để lại rất nhiều khoảng trắng rác (extra
        // spaces)
        // khi gặp các định dạng PDF phức tạp, chúng ta sẽ tối ưu hóa text tại đây.
        for (Document doc : rawDocuments) {
            String originalText = doc.getContent();
            // Loại bỏ khoảng trắng thừa (nhiều dấu cách thành 1) và trim
            String cleanedText = originalText.replaceAll("\\s+", " ").trim();

            // Cập nhật lại nội dung đã làm sạch vào Document (Dùng Reflection hoặc tạo mới
            // nếu cần)
            // Thay vì sửa trực tiếp (đôi khi List<Document> là immutable), ta map lại list.
        }

        List<Document> cleanedDocuments = rawDocuments.stream().map(doc -> {
            String cleanedText = doc.getContent().replaceAll("\\s+", " ").trim();
            return new Document(cleanedText, doc.getMetadata());
        }).filter(doc -> !doc.getContent().isEmpty()).toList();

        // 3. Phân rã văn bản thành các đoạn văn nhỏ (chunks)
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.apply(cleanedDocuments);

        // 4. Lưu vào ChromaDB theo từng cụm (Batch) nhỏ để dễ theo dõi

        int batchSize = 10;
        for (int i = 0; i < splitDocuments.size(); i += batchSize) {
            int end = Math.min(i + batchSize, splitDocuments.size());
            List<Document> batch = splitDocuments.subList(i, end);

            // Lệnh add() sẽ ngầm tự động gọi đến Embedding (Hiện tại là Transformers cục bộ
            // của hệ thống)
            // để dịch text thành ma trận số học (Vector), sau đó kết nối và đổ dữ liệu
            // thẳng vào ChromaDB
            vectorStore.add(batch);
        }
    }

    // --- Hỗ trợ việc kiểm tra DB (Xem VectorStore đã học mót được gì chưa) ---
    public String testSearchDb(String question) {
        // Tìm 2 đoạn văn bản (chunks) giống nhất với câu hỏi chứa trong DB
        List<Document> results = vectorStore.similaritySearch(
                org.springframework.ai.vectorstore.SearchRequest.query(question).withTopK(2));

        if (results == null || results.isEmpty()) {
            return "ChromaDB báo cáo: Không tìm thấy bất kỳ tri thức nào khớp với thông tin này! (DB rỗng hoặc câu hỏi không liên quan)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- CHROMA DB ĐÃ TÌM THẤY ").append(results.size()).append(" ĐOẠN KHỚP NHẤT ---\n\n");
        for (int i = 0; i < results.size(); i++) {
            sb.append("[Đoạn ").append(i + 1).append("]:\n");
            sb.append(results.get(i).getContent()).append("\n\n");
            sb.append("(MeataData: ").append(results.get(i).getMetadata().toString()).append(")\n");
            sb.append("----------------------------\n");
        }
        return sb.toString();
    }

    // --- CHỨC NĂNG CỐT LÕI: TƯ VẤN SINH VIÊN (RAG) ---
    public String chatWithAi(String question) {
        System.out.println("=== BAT DAU XU LY CAU HOI: " + question + " ===");
        System.out.println("--> [1] Bắt đầu tìm kiếm văn bản trong ChromaDB...");
        long startRetrieval = System.currentTimeMillis();

        // 1. TÌM KIẾM (Retrieval): Lục lọi trong trí nhớ (ChromaDB) xem có thông tin
        // nào liên quan đến câu hỏi không
        // Lấy ra 3 đoạn văn bản giống với câu hỏi nhất
        List<Document> relevantChunks = vectorStore.similaritySearch(
                org.springframework.ai.vectorstore.SearchRequest.query(question).withTopK(3));

        System.out.println("--> [2] CHROMA TÌM KIẾM HOÀN TẤT (Mất " + (System.currentTimeMillis() - startRetrieval)
                + "ms). Tìm thấy: " + relevantChunks.size() + " đoạn.");

        // 2. GOM NHẶT KIẾN THỨC
        StringBuilder contextBuilder = new StringBuilder();
        for (Document doc : relevantChunks) {
            contextBuilder.append(doc.getContent()).append("\n\n");
        }
        String context = contextBuilder.toString();

        // 3. ĐƯA TRI THỨC VÀO PROMPT (Tạo lệnh điều khiển AI)
        // Đây là bước quan trọng nhất của hệ thống RAG, chúng ta bắt AI phải nghe theo
        // chữ của mình
        String prompt = "Bạn là trợ lý tư vấn học tập thân thiện và chuyên nghiệp của trường Đại học Thăng Long. " +
                "Dưới đây là các tài liệu thông tin, quy chế do nhà trường cung cấp:\n" +
                "------------------\n" +
                context +
                "------------------\n" +
                "Dựa vào tài liệu duy nhất ở trên, hãy trả lời câu hỏi sau của sinh viên. " +
                "TUYỆT ĐỐI tuân thủ các quy tắc sau:\n" +
                "- Trả lời lịch sự, ngắn gọn và đúng trọng tâm.\n" +
                "- Chỉ lấy thông tin từ tài liệu được cung cấp ở trên.\n" +
                "- Tự động chỉnh sửa một văn phong mạch lạc, nối các từ bị cắt vỡ (nếu có) thành một câu hoàn chỉnh mà không làm mất đi ý nghĩa gốc.\n"
                +
                "- Nếu tài liệu không có câu trả lời, hãy nói: 'Rất tiếc, thông tin này không có trong quy chế hiện tại, bạn hãy liên hệ trực tiếp Bàn Tiếp sinh viên để được hỗ trợ nhé'. Không được tự bịa ra câu trả lời.\n\n"
                +
                "Câu hỏi của sinh viên: " + question;

        // 4. KẾT XUẤT (Generation): Đưa câu lệnh hoàn chỉnh vào thẳng Google Gemini
        // Native
        System.out.println("--> [3] Bắt đầu gọi API Google Gemini ĐỘC QUYỀN (Native)...");
        long startGen = System.currentTimeMillis();
        String result = callNativeGeminiApi(prompt);
        System.out.println(
                "--> [4] GEMINI API PHẢN HỒI HOÀN TẤT (Mất " + (System.currentTimeMillis() - startGen) + "ms).");
        return result;
    }

    private String callNativeGeminiApi(String promptText) {
        try {
            String url = geminiApiUrl + geminiApiKey;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", promptText);

            Map<String, Object> partObj = new HashMap<>();
            partObj.put("parts", Collections.singletonList(textPart));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", Collections.singletonList(partObj));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // Lấy chính xác phần nội dung chữ từ JSON trả về
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi từ chính Google API: " + e.getMessage();
        }
    }
}
