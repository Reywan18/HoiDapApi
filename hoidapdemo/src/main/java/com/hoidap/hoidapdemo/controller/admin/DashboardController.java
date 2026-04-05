package com.hoidap.hoidapdemo.controller.admin;

import com.hoidap.hoidapdemo.service.ReportServiceImpl;
import com.hoidap.hoidapdemo.dto.report.DashboardStats;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {
    private final ReportServiceImpl reportService;

    public DashboardController(ReportServiceImpl reportService) {
        this.reportService = reportService;
    }

    /**
     * Hiển thị trang Dashboard (Bảng điều khiển trung tâm).
     * Các đường dẫn "", "/", "/dashboard" đều trỏ về hàm này.
     * Nhiệm vụ:
     * 1. Gọi Service lấy số liệu thống kê (Tổng câu hỏi, tổng sinh viên...).
     * 2. Đẩy dữ liệu vào Model.
     * 3. Trả về giao diện admin/dashboard.html.
     */
    @GetMapping(value = {"", "/", "/dashboard"})
    public String showDashboard(Model model) {
        DashboardStats stats = reportService.getDashboardStats();

        model.addAttribute("stats", stats);

        return "admin/dashboard";
    }
}
