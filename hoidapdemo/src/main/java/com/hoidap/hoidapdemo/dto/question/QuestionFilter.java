package com.hoidap.hoidapdemo.dto.question;

import com.hoidap.hoidapdemo.entity.enums.QuestionStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class QuestionFilter {
    private String keyword;
    private QuestionStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    private String maSv;
    private String maCv;
}
