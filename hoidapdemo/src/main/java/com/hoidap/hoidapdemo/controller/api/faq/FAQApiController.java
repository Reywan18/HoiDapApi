package com.hoidap.hoidapdemo.controller.api.faq;

import com.hoidap.hoidapdemo.entity.faq.FAQJpaEntity;
import com.hoidap.hoidapdemo.repository.faq.FAQJpaRepository;
import com.hoidap.hoidapdemo.repository.faq.FAQSpecification;
import com.hoidap.hoidapdemo.utils.AppStatus;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.dto.faq.FAQFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
public class FAQApiController {
    private final FAQJpaRepository faqRepo;

    public FAQApiController(FAQJpaRepository faqRepo) {
        this.faqRepo = faqRepo;
    }

    // API: Lấy danh sách FAQ
    @GetMapping
    public ResponseEntity<ApiResponse<List<FAQJpaEntity>>> getAllFAQs(@ModelAttribute FAQFilter filter) {
        List<FAQJpaEntity> data = faqRepo.findAll(FAQSpecification.filter(filter));

        return ResponseEntity.ok(ApiResponse.<List<FAQJpaEntity>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách FAQ thành công")
                .data(data)
                .build());
    }

    // API: Xem chi tiết 1 FAQ
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FAQJpaEntity>> getFAQDetail(@PathVariable Long id) {
        FAQJpaEntity faq = faqRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ không tồn tại"));

        return ResponseEntity.ok(ApiResponse.<FAQJpaEntity>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy chi tiết thành công")
                .data(faq)
                .build());
    }
}
