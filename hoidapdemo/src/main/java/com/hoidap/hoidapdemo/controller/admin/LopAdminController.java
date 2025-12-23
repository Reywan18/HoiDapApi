package com.hoidap.hoidapdemo.controller.admin;

import com.hoidap.hoidapdemo.service.port.LopServicePort;
import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.utils.excel.ExcelHelper;
import com.hoidap.hoidapdemo.dto.lop.CreateLopRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/admin/lop")
public class LopAdminController {
    private final LopServicePort lopService;
    private final CVHTJpaRepository cvhtRepo;
    public LopAdminController(LopServicePort lopService, CVHTJpaRepository cvhtRepo) {
        this.lopService = lopService;
        this.cvhtRepo = cvhtRepo;
    }

    @GetMapping
    public String listLop(Model model) {
        model.addAttribute("lops", lopService.getAllLop());
        return "admin/lop/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("lopRequest", new CreateLopRequest());
        model.addAttribute("isEdit", false);
        model.addAttribute("listCvht", cvhtRepo.findAll());
        return "admin/lop/form";
    }

    @PostMapping("/save")
    public String saveLop(@ModelAttribute CreateLopRequest request) {
        lopService.createLop(request);
        return "redirect:/admin/lop";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        LopJpaEntity lop = lopService.getLopById(id);

        CreateLopRequest dto = new CreateLopRequest();
        dto.setMaLop(lop.getMaLop());
        dto.setKhoaHoc(lop.getKhoaHoc());
        dto.setChuyenNganh(lop.getChuyenNganh());

        if (lop.getCvht() != null) {
            dto.setMaCvht(lop.getCvht().getMaCv());
        }

        model.addAttribute("lopRequest", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("listCvht", cvhtRepo.findAll());
        return "admin/lop/form";
    }

    @PostMapping("/update/{id}")
    public String updateLop(@PathVariable String id, @ModelAttribute CreateLopRequest request) {
        lopService.updateLop(id, request);
        return "redirect:/admin/lop";
    }

    @GetMapping("/delete/{id}")
    public String deleteLop(@PathVariable String id) {
        lopService.deleteLop(id);
        return "redirect:/admin/lop";
    }

    //Export
    @GetMapping("/export")
    public ResponseEntity<Resource> exportExcel() {
        String filename = "danh_sach_lop.xlsx";
        InputStreamResource file = new InputStreamResource(ExcelHelper.lopsToExcel(lopService.getAllLop()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    //Import
    @PostMapping("/import")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                List<LopJpaEntity> listLop = ExcelHelper.excelToLops(file.getInputStream());

                lopService.saveListLop(listLop);

                return "redirect:/admin/lop";
            } catch (Exception e) {
                model.addAttribute("message", "Không thể upload file: " + e.getMessage());
                return "admin/lop/list";
            }
        }
        model.addAttribute("message", "Vui lòng chọn file Excel chuẩn (.xlsx)!");
        return "admin/lop/list";
    }
}
