package com.hoidap.hoidapdemo.controller.api.admin;

import com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.repository.chat.ConversationJpaRepository;
import com.hoidap.hoidapdemo.repository.chat.MessageJpaRepository;
import com.hoidap.hoidapdemo.dto.chat.ConversationResponseDto;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/questions")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminQuestionApiController {

    private final ConversationJpaRepository conversationRepo;
    private final MessageJpaRepository messageRepo;

    public AdminQuestionApiController(ConversationJpaRepository conversationRepo, MessageJpaRepository messageRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ConversationResponseDto>>> getAllQuestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<ConversationJpaEntity> entityPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            entityPage = conversationRepo.searchAllConversations(keyword.trim(), pageable);
        } else {
            entityPage = conversationRepo.findAll(pageable);
        }

        org.springframework.data.domain.Page<ConversationResponseDto> dtoPage = entityPage.map(this::mapToDto);

        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ConversationResponseDto>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(dtoPage)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationResponseDto>> getQuestion(@PathVariable Long id) {
        ConversationJpaEntity conversation = conversationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hội thoại: " + id));
        return ResponseEntity.ok(ApiResponse.<ConversationResponseDto>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(mapToDto(conversation))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationResponseDto>> updateQuestion(@PathVariable Long id, @RequestBody ConversationJpaEntity updated) {
        ConversationJpaEntity existing = conversationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hội thoại: " + id));
        
        existing.setTieuDe(updated.getTieuDe());
        // Quản trị viên chủ yếu sửa đổi tên hoặc thông tin cơ bản.
        // Có thể mở rộng để sửa đổi trạng thái nếu cần.
        existing.setTrangThai(updated.getTrangThai());
        
        conversationRepo.save(existing);
        
        return ResponseEntity.ok(ApiResponse.<ConversationResponseDto>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Cập nhật hội thoại thành công!")
                .data(mapToDto(existing))
                .build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Long id) {
        if (!conversationRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy hội thoại: " + id);
        }
        // Phải xóa message trước khi xóa conversation do khóa ngoại
        messageRepo.deleteByConversation_Id(id);
        conversationRepo.deleteById(id);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã xóa hội thoại (câu hỏi) và tin nhắn liên quan!")
                .build());
    }

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
}
