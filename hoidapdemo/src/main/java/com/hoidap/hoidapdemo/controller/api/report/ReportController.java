package com.hoidap.hoidapdemo.controller.api.report;

import com.hoidap.hoidapdemo.service.ReportServiceImpl;
import com.hoidap.hoidapdemo.utils.pdf.PdfReportGenerator;
import com.hoidap.hoidapdemo.dto.common.ApiResponse;
import com.hoidap.hoidapdemo.dto.report.DashboardStats;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportServiceImpl reportService;

    public ReportController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboard() {
        DashboardStats stats = reportService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.<DashboardStats>builder()
                .status(200)
                .message("Lấy báo cáo thành công")
                .data(stats)
                .build());
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportPdf() {
        DashboardStats stats = reportService.getDashboardStats();
        ByteArrayInputStream bis = PdfReportGenerator.exportStatsToPdf(stats);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=bao_cao_he_thong.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
