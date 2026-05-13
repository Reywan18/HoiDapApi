package com.hoidap.hoidapdemo.controller.api.chat;

import com.hoidap.hoidapdemo.dto.chat.ConversationResponseDto;
import com.hoidap.hoidapdemo.dto.chat.MessageResponseDto;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.dto.chat.CreateConversationRequestDto;
import com.hoidap.hoidapdemo.repository.lop.LopJpaRepository;
import com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity;
import com.hoidap.hoidapdemo.entity.chat.MessageJpaEntity;
import com.hoidap.hoidapdemo.entity.enums.ConversationStatus;
import com.hoidap.hoidapdemo.entity.enums.SenderType;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.repository.chat.ConversationJpaRepository;
import com.hoidap.hoidapdemo.repository.chat.MessageJpaRepository;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import com.hoidap.hoidapdemo.service.port.AiServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationJpaRepository conversationRepo;
    private final MessageJpaRepository messageRepo;
    private final SinhVienJpaRepository sinhVienRepo;
    private final LopJpaRepository lopRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final AiServicePort aiService;

    public ConversationController(ConversationJpaRepository conversationRepo,
            MessageJpaRepository messageRepo,
            SinhVienJpaRepository sinhVienRepo,
            LopJpaRepository lopRepo,
            SimpMessagingTemplate messagingTemplate,
            AiServicePort aiService) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.sinhVienRepo = sinhVienRepo;
        this.lopRepo = lopRepo;
        this.messagingTemplate = messagingTemplate;
        this.aiService = aiService;
    }

    // 1. Lấy danh sách phòng chat của 1 sinh viên
    @GetMapping("/student/{maSv}")
    public ResponseEntity<ApiResponse<Page<ConversationResponseDto>>> getStudentConversations(
            @PathVariable String maSv,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword) {

        Page<ConversationResponseDto> responsePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            responsePage = conversationRepo
                    .findBySinhVien_MaSvAndTrangThaiNotAndTieuDeContainingIgnoreCase(maSv,
                            ConversationStatus.CHATTING_WITH_BOT, keyword.trim(), PageRequest.of(page, size))
                    .map(this::mapToDto);
        } else {
            responsePage = conversationRepo
                    .findBySinhVien_MaSvAndTrangThaiNot(maSv, ConversationStatus.CHATTING_WITH_BOT,
                            PageRequest.of(page, size))
                    .map(this::mapToDto);
        }

        return ResponseEntity.ok(ApiResponse.<Page<ConversationResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách thành công")
                .data(responsePage)
                .build());
    }

    // 2. Lấy danh sách phòng chat mà CVHT đang quản lý (Hỗ trợ lọc)
    @GetMapping("/cvht/{maCv}")
    public ResponseEntity<ApiResponse<Page<ConversationResponseDto>>> getCVHTConversations(
            @PathVariable String maCv,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String maLop,
            @RequestParam(required = false) String keyword) {

        Specification<ConversationJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc theo mã CVHT
            predicates.add(cb.equal(root.get("cvht").get("maCv"), maCv));

            // Loại trừ Chat với Bot mặc định (hoặc theo yêu cầu hệ thống)
            predicates.add(cb.notEqual(root.get("trangThai"), ConversationStatus.CHATTING_WITH_BOT));

            // Lọc theo trạng thái nếu có
            if (trangThai != null && !trangThai.trim().isEmpty() && !trangThai.equalsIgnoreCase("ALL")) {
                if (trangThai.equalsIgnoreCase("OPEN")) {
                    predicates.add(cb.notEqual(root.get("trangThai"), ConversationStatus.RESOLVED));
                    predicates.add(cb.notEqual(root.get("trangThai"), ConversationStatus.REPORTED));
                } else {
                    try {
                        predicates.add(
                                cb.equal(root.get("trangThai"), ConversationStatus.valueOf(trangThai.toUpperCase())));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            // Lọc theo mã lớp nếu có
            if (maLop != null && !maLop.trim().isEmpty() && !maLop.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(root.get("sinhVien").get("lop").get("maLop"), maLop));
            }

            // Lọc theo từ khóa (tiêu đề hoặc tên sinh viên)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("tieuDe")), pattern);
                Predicate studentNameLike = cb.like(cb.lower(root.get("sinhVien").get("hoTen")), pattern);
                predicates.add(cb.or(titleLike, studentNameLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ConversationResponseDto> responsePage = conversationRepo.findAll(spec, PageRequest.of(page, size))
                .map(this::mapToDto);

        return ResponseEntity.ok(ApiResponse.<Page<ConversationResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách thành công")
                .data(responsePage)
                .build());
    }

    // Lấy danh sách lớp mà CVHT quản lý để hiển thị bộ lọc (Lấy trực tiếp từ bảng
    // Lớp và Sắp xếp)
    @GetMapping("/cvht/{maCv}/classes")
    public ResponseEntity<ApiResponse<List<String>>> getAdvisorClasses(@PathVariable String maCv) {
        List<String> classes = lopRepo.findByCvhtId(maCv)
                .stream()
                .map(LopJpaEntity::getMaLop)
                .sorted() // Sắp xếp A-Z
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách lớp thành công")
                .data(classes)
                .build());
    }

    // 3. Lấy toàn bộ lịch sử tin nhắn khi bấm vào 1 phòng chat
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponseDto>>> getMessageHistory(@PathVariable Long conversationId) {
        List<MessageResponseDto> list = messageRepo.findByConversation_IdOrderByThoiGianGuiAsc(conversationId)
                .stream()
                .map(this::mapToMessageDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<MessageResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy lịch sử tin nhắn thành công")
                .data(list)
                .build());
    }

    // 4. Lấy thông tin chi tiết 1 phòng chat
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationResponseDto>> getConversationDetail(@PathVariable Long id) {
        ConversationJpaEntity entity = conversationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        return ResponseEntity.ok(ApiResponse.<ConversationResponseDto>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy thông tin thành công")
                .data(mapToDto(entity))
                .build());
    }

    // 5. Kết thúc câu hỏi / Đánh dấu đã giải quyết
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveConversation(@PathVariable Long id) {
        ConversationJpaEntity entity = conversationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        entity.setTrangThai(ConversationStatus.RESOLVED);
        entity.setNgayCapNhatCuoi(LocalDateTime.now());
        conversationRepo.save(entity);

        // --- Cập nhật kiến thức vào AI ChromaDB ---
        try {
            List<MessageJpaEntity> messages = messageRepo.findByConversation_IdOrderByThoiGianGuiAsc(id);
            if (messages != null && !messages.isEmpty()) {
                
                StringBuilder fullConversationText = new StringBuilder();
                fullConversationText.append("Hỏi đáp giữa Sinh viên và Cố vấn học tập:\n");
                
                for (MessageJpaEntity msg : messages) {
                    if (msg.getNguoiGuiType() == SenderType.SINH_VIEN) {
                        fullConversationText.append("Sinh viên: ").append(msg.getNoiDung()).append("\n");
                    } else {
                        fullConversationText.append("CVHT: ").append(msg.getNoiDung()).append("\n");
                    }
                }
                
                // Lấy Tiêu đề của cuộc trò chuyện làm câu hỏi trọng tâm để check trùng lặp
                String questionToCheck = entity.getTieuDe();
                
                // Gọi AI Service để lưu
                aiService.saveTextToDb(questionToCheck, fullConversationText.toString());
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật kiến thức vào ChromaDB: " + e.getMessage());
            // Không throw exception để không làm gián đoạn luồng API chính
        }

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã đánh dấu câu hỏi là hoàn thành")
                .build());
    }

    // 6. Sinh viên tạo nhanh 1 câu hỏi mới (Hỗ trợ đính kèm tệp ngay lúc tạo)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createConversation(
            @RequestPart("tieuDe") String tieuDe,
            @RequestPart("noiDung") String noiDung,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // Lấy Email từ Token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        // Tìm Sinh Viên dựa vào Email trong Token
        SinhVienJpaEntity sv = sinhVienRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Sinh Viên"));

        // 1. Khởi tạo phòng chat
        ConversationJpaEntity conversation = new ConversationJpaEntity();
        conversation.setSinhVien(sv);
        // Lấy CVHT từ Lớp của Sinh viên này nếu có
        if (sv.getLop() != null && sv.getLop().getCvht() != null) {
            conversation.setCvht(sv.getLop().getCvht());
        }
        conversation.setTieuDe(tieuDe);
        conversation.setTrangThai(ConversationStatus.CHATTING_WITH_CVHT);
        conversation.setNgayTao(LocalDateTime.now());
        conversation.setNgayCapNhatCuoi(LocalDateTime.now());

        conversation = conversationRepo.save(conversation);

        // 2. Tạo tin nhắn đầu tiên lưu vào DB
        MessageJpaEntity firstMessage = new MessageJpaEntity();
        firstMessage.setConversation(conversation);
        firstMessage.setNguoiGuiType(SenderType.SINH_VIEN);
        firstMessage.setNguoiGuiId(sv.getMaSv());
        firstMessage.setNoiDung(noiDung);
        firstMessage.setThoiGianGui(LocalDateTime.now());

        // Nếu có tệp đính kèm
        if (file != null && !file.isEmpty()) {
            try {
                firstMessage.setFileName(file.getOriginalFilename());
                firstMessage.setFileType(file.getContentType());
                firstMessage.setFileData(file.getBytes());
                // Nếu nội dung trống, dùng tên file làm nội dung
                if (noiDung == null || noiDung.trim().isEmpty()) {
                    firstMessage.setNoiDung("Đã đính kèm tệp: " + file.getOriginalFilename());
                }
            } catch (java.io.IOException e) {
                System.err.println("Lỗi lưu file khi tạo hội thoại: " + e.getMessage());
            }
        }

        messageRepo.save(firstMessage);

        return ResponseEntity.ok(ApiResponse.<ConversationResponseDto>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo phòng chat thành công")
                .data(mapToDto(conversation))
                .build());
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<MessageResponseDto>> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") String senderId,
            @RequestParam("senderType") String senderType,
            @RequestParam(value = "content", required = false) String content) {
        try {
            ConversationJpaEntity conversation = conversationRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

            MessageJpaEntity messageEntity = new MessageJpaEntity();
            messageEntity.setConversation(conversation);

            if (content != null && !content.trim().isEmpty()) {
                messageEntity.setNoiDung(content);
            } else {
                messageEntity.setNoiDung("Đã đính kèm tệp: " + file.getOriginalFilename());
            }

            messageEntity.setThoiGianGui(LocalDateTime.now());
            messageEntity.setNguoiGuiId(senderId);
            messageEntity.setNguoiGuiType(SenderType.valueOf(senderType));
            messageEntity.setFileName(file.getOriginalFilename());
            messageEntity.setFileType(file.getContentType());
            messageEntity.setFileData(file.getBytes());

            MessageJpaEntity savedMessage = messageRepo.save(messageEntity);
            conversation.setNgayCapNhatCuoi(LocalDateTime.now());
            conversationRepo.save(conversation);

            MessageResponseDto responseDto = mapToMessageDto(savedMessage);
            messagingTemplate.convertAndSend("/topic/conversation/" + id, responseDto);

            return ResponseEntity.ok(ApiResponse.<MessageResponseDto>builder()
                    .status(AppStatus.SUCCESS.getCode())
                    .message("Tải lên thành công")
                    .data(responseDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<MessageResponseDto>builder()
                    .status(AppStatus.INTERNAL_ERROR.getCode())
                    .message("Lỗi khi tải lên file: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/messages/{messageId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageId) {
        MessageJpaEntity message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));

        if (message.getFileData() == null) {
            throw new RuntimeException("Tin nhắn không có file đính kèm");
        }

        ByteArrayResource resource = new ByteArrayResource(
                message.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + message.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        message.getFileType() != null ? message.getFileType() : "application/octet-stream"))
                .body(resource);
    }

    // Hàm chuyển đổi Entity sang DTO
    private ConversationResponseDto mapToDto(ConversationJpaEntity entity) {
        ConversationResponseDto dto = new ConversationResponseDto();
        dto.setId(entity.getId());
        dto.setTieuDe(entity.getTieuDe());
        dto.setTrangThai(entity.getTrangThai() != null ? entity.getTrangThai().name() : null);
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgayCapNhatCuoi(entity.getNgayCapNhatCuoi());

        if (entity.getSinhVien() != null) {
            SinhVienJpaEntity sv = entity.getSinhVien();
            dto.setMaSv(sv.getMaSv());
            dto.setTenSv(sv.getHoTen());
            dto.setEmailSv(sv.getEmail());
            dto.setSdtSv(sv.getSoDienThoai());
            if (sv.getLop() != null) {
                dto.setMaLopSv(sv.getLop().getMaLop());
                dto.setKhoaSv(sv.getLop().getChuyenNganh());
            }
        }

        if (entity.getCvht() != null) {
            CVHTJpaEntity cv = entity.getCvht();
            dto.setMaCv(cv.getMaCv());
            dto.setTenCv(cv.getHoTen());
            dto.setEmailCv(cv.getEmail());
            dto.setSdtCv(cv.getSoDienThoai());
            dto.setChuyenMonCv(cv.getChuyenMon());
        }

        return dto;
    }

    private MessageResponseDto mapToMessageDto(MessageJpaEntity entity) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setId(entity.getId());
        dto.setConversationId(entity.getConversation() != null ? entity.getConversation().getId() : null);
        dto.setNguoiGuiType(entity.getNguoiGuiType() != null ? entity.getNguoiGuiType().name() : null);
        dto.setNguoiGuiId(entity.getNguoiGuiId());
        dto.setNoiDung(entity.getNoiDung());
        dto.setThoiGianGui(entity.getThoiGianGui());
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        return dto;
    }
}
