package com.hoidap.hoidapdemo.controller.api.admin;

import com.hoidap.hoidapdemo.entity.faq.FAQJpaEntity;
import com.hoidap.hoidapdemo.repository.faq.FAQJpaRepository;
import com.hoidap.hoidapdemo.repository.faq.FAQSpecification;
import com.hoidap.hoidapdemo.dto.faq.FAQFilter;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.utils.AppStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/faqs")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminFAQApiController {

    private final FAQJpaRepository faqRepo;

    public AdminFAQApiController(FAQJpaRepository faqRepo) {
        this.faqRepo = faqRepo;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<FAQJpaEntity>>> getAllFAQs(
            @ModelAttribute FAQFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "maFaq"));
        
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<FAQJpaEntity>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .data(faqRepo.findAll(FAQSpecification.filter(filter), pageable))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FAQJpaEntity>> createFAQ(@RequestBody FAQJpaEntity faq) {
        FAQJpaEntity savedFaq = faqRepo.save(faq);
        return ResponseEntity.ok(ApiResponse.<FAQJpaEntity>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Tạo FAQ thành công!")
                .data(savedFaq)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FAQJpaEntity>> updateFAQ(@PathVariable Long id, @RequestBody FAQJpaEntity faq) {
        faq.setMaFaq(id);
        FAQJpaEntity savedFaq = faqRepo.save(faq);
        return ResponseEntity.ok(ApiResponse.<FAQJpaEntity>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Cập nhật FAQ thành công!")
                .data(savedFaq)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFAQ(@PathVariable Long id) {
        faqRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đã xóa FAQ!")
                .build());
    }
}
