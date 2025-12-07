package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.api.question;

import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.application.service.QuestionServiceImpl;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.answer.AnswerVersionJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.question.QuestionJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.answer.AnswerJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.answer.AnswerVersionJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.common.AppStatus;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.answer.AnswerHistoryResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.answer.AnswerRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.answer.LatestAnswerResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.common.PageResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionFilter;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.user.UserDto;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Quản Lý Câu Hỏi", description = "Các API liên quan đến hỏi đáp giữa Sinh viên và CVHT")
public class QuestionController {
    private final QuestionServiceImpl questionService;
    private final UserServicePort userService;
    private final AnswerJpaRepository answerRepo;
    private final AnswerVersionJpaRepository answerVersionRepo;

    public QuestionController(QuestionServiceImpl questionService, UserServicePort userService, AnswerJpaRepository answerRepo, AnswerVersionJpaRepository answerVersionRepo) {
        this.questionService = questionService;
        this.userService = userService;
        this.answerRepo = answerRepo;
        this.answerVersionRepo = answerVersionRepo;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sinh viên đặt câu hỏi mới", description = "API này cho phép sinh viên gửi câu hỏi kèm file đính kèm.")
    public ResponseEntity<?> createQuestion(
            @ModelAttribute @Valid QuestionRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        UserDto userDto = userService.getUserByEmail(email);
        questionService.createQuestion(userDto.getMaDinhDanh(), request);
        ApiResponse response = ApiResponse.builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Đặt câu hỏi thành công!")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách câu hỏi (Có phân trang)", description = "Hỗ trợ lọc theo từ khóa, trạng thái, ngày tháng...")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> getAllQuestions(
            @ModelAttribute QuestionFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<QuestionResponse> pageData = questionService.getAllQuestions(filter, page, size);

        ApiResponse<PageResponse<QuestionResponse>> response = ApiResponse.<PageResponse<QuestionResponse>>builder()
                .status(AppStatus.SUCCESS.getCode())
                .message("Lấy danh sách thành công")
                .data(pageData)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết câu hỏi", description = "Lấy thông tin chi tiết dựa trên ID câu hỏi")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        QuestionJpaEntity q = questionService.getQuestionEntityById(id);

        if (q.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(q.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + q.getFileName() + "\"")
                .body(new ByteArrayResource(q.getFileData()));
    }

    @GetMapping("/versions/{versionId}/file")
    public ResponseEntity<Resource> downloadAnswerVersionFile(@PathVariable Long versionId) {
        AnswerVersionJpaEntity version = answerVersionRepo.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên bản trả lời không tồn tại với ID: " + versionId));
        if (version.getFileData() == null || version.getFileData().length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(version.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getFileName() + "\"")
                .body(new ByteArrayResource(version.getFileData()));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "CVHT cập nhật câu trả lời", description = "CVHT cập nhật nội dung trả lời và file đính kèm cho sinh viên")
    public ResponseEntity<ApiResponse> updateQuestion(
            @PathVariable Long id,
            @ModelAttribute @Valid QuestionRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        UserDto userDto = userService.getUserByEmail(email);

        questionService.updateQuestion(id, userDto.getMaDinhDanh(), request);

        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .message("Cập nhật câu hỏi thành công!")
                .build());
    }

    @PostMapping(value = "/{id}/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "CVHT trả lời câu hỏi", description = "CVHT gửi nội dung trả lời và file đính kèm cho sinh viên")
    public ResponseEntity<ApiResponse> answerQuestion(
            @PathVariable Long id,
            @ModelAttribute @Valid AnswerRequest request,
            Authentication authentication) {

        String emailCvht = authentication.getName();
        questionService.answerQuestion(id, emailCvht, request);

        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .message("Trả lời thành công!")
                .build());
    }

    @GetMapping("/{id}/answers")
    @Operation(summary = "Lịch sử câu trả lời", description = "Sinh viên xem lịch sử câu trả lời")
    public ResponseEntity<ApiResponse<List<AnswerHistoryResponse>>> getAnswerHistory(@PathVariable Long id) {

        var answerOpt = answerRepo.findByQuestion_MaCauHoi(id);
        if (answerOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.<List<AnswerHistoryResponse>>builder()
                    .status(200).message("Chưa có câu trả lời nào").data(List.of()).build());
        }

        List<AnswerVersionJpaEntity> history = answerVersionRepo
                .findByAnswer_IdOrderByVersionDesc(answerOpt.get().getId());

        List<AnswerHistoryResponse> response = history.stream().map(v -> {
            String downloadUrl = null;
            if (v.getFileData() != null) {
                downloadUrl = "/api/questions/versions/" + v.getId() + "/file";
            }

            return AnswerHistoryResponse.builder()
                    .version(v.getVersion())
                    .noiDung(v.getNoiDung())
                    .thoiGianTao(v.getThoiGianTao())
                    .fileName(v.getFileName())
                    .downloadUrl(downloadUrl)
                    .build();
        }).toList();

        return ResponseEntity.ok(ApiResponse.<List<AnswerHistoryResponse>>builder()
                .status(200)
                .data(response)
                .build());
    }

    @GetMapping("/{id}/latest-answer")
    @Operation(summary = "Câu trả lời mới nhất", description = "Sinh Viên sẽ xem câu trả lời mới nhất")
    public ResponseEntity<ApiResponse<LatestAnswerResponse>> getLatestAnswer(@PathVariable Long id) {

        LatestAnswerResponse response = questionService.getLatestAnswer(id);

        if (response == null) {
            return ResponseEntity.ok(ApiResponse.<LatestAnswerResponse>builder()
                    .status(200)
                    .message("Câu hỏi chưa được trả lời")
                    .data(null)
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.<LatestAnswerResponse>builder()
                .status(200)
                .message("Lấy câu trả lời thành công")
                .data(response)
                .build());
    }
}
