package com.hoidap.hoidapdemo.controller.api.report;

import com.hoidap.hoidapdemo.dto.report.BaoCaoDetailDto;
import com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity;
import com.hoidap.hoidapdemo.entity.report.BaoCaoJpaEntity;
import com.hoidap.hoidapdemo.repository.BaoCaoRepository;
import com.hoidap.hoidapdemo.repository.chat.ConversationJpaRepository;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReportIssueController {

    private final BaoCaoRepository baoCaoRepository;
    private final ConversationJpaRepository conversationRepository;

    public ReportIssueController(BaoCaoRepository baoCaoRepository,
                                 ConversationJpaRepository conversationRepository) {
        this.baoCaoRepository = baoCaoRepository;
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/issues/report")
    @PreAuthorize("hasAuthority('CVHT')")
    public ResponseEntity<ApiResponse<String>> createReport(@RequestBody Map<String, Object> payload) {
        Long conversationId = Long.valueOf(payload.get("conversationId").toString());
        String reason = payload.getOrDefault("reason", "").toString();

        BaoCaoJpaEntity baoCao = BaoCaoJpaEntity.builder()
                .conversationId(conversationId)
                .lyDo(reason)
                .thoiGianBaoCao(LocalDateTime.now())
                .trangThai("PENDING")
                .build();

        baoCaoRepository.save(baoCao);

        // Cập nhật trạng thái conversation sang REPORTED
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setTrangThai(com.hoidap.hoidapdemo.entity.enums.ConversationStatus.REPORTED);
            conversationRepository.save(conv);
        });

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .message("Đã ghi nhận báo cáo")
                .build());
    }

    @GetMapping("/admin/issues")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<BaoCaoDetailDto>>> getAllReports() {
        List<BaoCaoJpaEntity> reports = baoCaoRepository.findAll();
        // Sắp xếp mới nhất lên đầu
        reports.sort((a, b) -> b.getThoiGianBaoCao().compareTo(a.getThoiGianBaoCao()));

        List<BaoCaoDetailDto> result = reports.stream().map(r -> {
            // JOIN với bảng conversation để lấy thông tin CVHT và SV
            ConversationJpaEntity conv = conversationRepository.findById(r.getConversationId()).orElse(null);

            String maCv = null, tenCv = null, maSv = null, tenSv = null, tieuDe = null;
            if (conv != null) {
                if (conv.getCvht() != null) {
                    maCv = conv.getCvht().getMaCv();
                    tenCv = conv.getCvht().getHoTen();
                }
                if (conv.getSinhVien() != null) {
                    maSv = conv.getSinhVien().getMaSv();
                    tenSv = conv.getSinhVien().getHoTen();
                }
                tieuDe = conv.getTieuDe();
            }

            return BaoCaoDetailDto.builder()
                    .id(r.getId())
                    .conversationId(r.getConversationId())
                    .tieuDeConversation(tieuDe)
                    .maCv(maCv)
                    .tenCv(tenCv)
                    .maSv(maSv)
                    .tenSv(tenSv)
                    .lyDo(r.getLyDo())
                    .thoiGianBaoCao(r.getThoiGianBaoCao())
                    .trangThai(r.getTrangThai())
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<BaoCaoDetailDto>>builder()
                .status(200)
                .data(result)
                .build());
    }

    @PutMapping("/admin/issues/{id}/resolve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<String>> resolveReport(@PathVariable Long id) {
        BaoCaoJpaEntity baoCao = baoCaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo cáo"));
        baoCao.setTrangThai("RESOLVED");
        baoCaoRepository.save(baoCao);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .message("Đã xử lý báo cáo")
                .build());
    }
}
