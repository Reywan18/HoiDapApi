package com.hoidap.hoidapdemo.dto.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnswerRequest {
    @NotBlank(message = "Nội dung trả lời không được để trống")
    private String noiDung;

    private MultipartFile file;
}
