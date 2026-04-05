package com.hoidap.hoidapdemo.controller.api.chat;

import com.hoidap.hoidapdemo.dto.chat.ConversationResponseDto;
import com.hoidap.hoidapdemo.dto.chat.MessageResponseDto;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import com.hoidap.hoidapdemo.dto.chat.CreateConversationRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationJpaRepository conversationRepo;
    private final MessageJpaRepository messageRepo;
    private final SinhVienJpaRepository sinhVienRepo;

    public ConversationController(ConversationJpaRepository conversationRepo,
            MessageJpaRepository messageRepo,
            SinhVienJpaRepository sinhVienRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.sinhVienRepo = sinhVienRepo;
    }

    // 1. Lấy danh sách phòng chat của 1 sinh viên
    @GetMapping("/student/{maSv}")
    public ResponseEntity<ApiResponse<Page<ConversationResponseDto>>> getStudentConversations(
            @PathVariable String maSv,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ConversationResponseDto> responsePage = conversationRepo
                .findBySinhVien_MaSvAndTrangThaiNot(maSv, ConversationStatus.CHATTING_WITH_BOT,
                        PageRequest.of(page, size))
                .map(this::mapToDto);

        return ResponseEntity.ok(ApiResponse.<Page<ConversationResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách thành công")
                .data(responsePage)
                .build());
    }

    // 2. Lấy danh sách phòng chat mà CVHT đang quản lý
    @GetMapping("/cvht/{maCv}")
    public ResponseEntity<ApiResponse<Page<ConversationResponseDto>>> getCVHTConversations(
            @PathVariable String maCv,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ConversationResponseDto> responsePage = conversationRepo
                .findByCvht_MaCvAndTrangThaiNot(maCv, ConversationStatus.CHATTING_WITH_BOT, PageRequest.of(page, size))
                .map(this::mapToDto);

        return ResponseEntity.ok(ApiResponse.<Page<ConversationResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách thành công")
                .data(responsePage)
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

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã đánh dấu câu hỏi là hoàn thành")
                .build());
    }

    // 6. Sinh viên tạo nhanh 1 câu hỏi mới
    @PostMapping
    public ResponseEntity<?> createConversation(@RequestBody CreateConversationRequestDto request) {
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
        conversation.setTieuDe(request.getTieuDe());
        conversation.setTrangThai(ConversationStatus.CHATTING_WITH_CVHT); // Có thể đổi sang BOT nếu có AI
        conversation.setNgayTao(LocalDateTime.now());
        conversation.setNgayCapNhatCuoi(LocalDateTime.now());

        conversation = conversationRepo.save(conversation);

        // 2. Tạo tin nhắn đầu tiên lưu vào DB
        MessageJpaEntity firstMessage = new MessageJpaEntity();
        firstMessage.setConversation(conversation);
        firstMessage.setNguoiGuiType(SenderType.SINH_VIEN);
        firstMessage.setNguoiGuiId(sv.getMaSv());
        firstMessage.setNoiDung(request.getNoiDung());
        firstMessage.setThoiGianGui(LocalDateTime.now());

        messageRepo.save(firstMessage);

        return ResponseEntity.ok(ApiResponse.<ConversationResponseDto>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo phòng chat thành công")
                .data(mapToDto(conversation))
                .build());
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
