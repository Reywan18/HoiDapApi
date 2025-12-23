package com.hoidap.hoidapdemo.dto.question;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuestionRequest {
    private String tieuDe;
    private String noiDung;
    private MultipartFile file;
}
