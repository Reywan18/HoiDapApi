package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.admin;

import com.hoidap.hoidapdemo.application.service.ReportServiceImpl;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.report.DashboardStats;
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

    @GetMapping(value = {"", "/", "/dashboard"})
    public String showDashboard(Model model) {
        DashboardStats stats = reportService.getDashboardStats();

        model.addAttribute("stats", stats);

        return "admin/dashboard";
    }
}
