package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.admin;

import com.hoidap.hoidapdemo.application.port.LopServicePort;
import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class UserAdminController {
    private final UserServicePort userService;
    private final LopServicePort lopService;

    public UserAdminController(UserServicePort userService, LopServicePort lopService) {
        this.lopService = lopService;
        this.userService = userService;
    }

    //Quan ly Sinh Vien
    @GetMapping("/sinhvien")
    public String listSinhVien(Model model) {
        model.addAttribute("listSV", userService.getAllSinhVien());
        return "admin/sinhvien/list";
    }

    @GetMapping("/sinhvien/create")
    public String formCreateSV(Model model) {
        model.addAttribute("sinhVien", new SinhVienJpaEntity());
        model.addAttribute("listLop", lopService.getAllLop());
        model.addAttribute("isEdit", false);
        return "admin/sinhvien/form";
    }

    @GetMapping("/sinhvien/edit/{id}")
    public String formEditSV(@PathVariable String id, Model model) {
        SinhVienJpaEntity sv = userService.getSinhVienById(id);
        sv.setPassword("");

        model.addAttribute("sinhVien", sv);
        model.addAttribute("listLop", lopService.getAllLop());
        model.addAttribute("isEdit", true);
        return "admin/sinhvien/form";
    }

    @PostMapping("/sinhvien/save")
    public String saveSinhVien(@ModelAttribute SinhVienJpaEntity sv) {
        userService.saveSinhVien(sv);
        return "redirect:/admin/sinhvien";
    }

    @GetMapping("/sinhvien/delete/{id}")
    public String deleteSinhVien(@PathVariable String id) {
        userService.deleteSinhVien(id);
        return "redirect:/admin/sinhvien";
    }

    //Quan ly Cvht
    @GetMapping("/cvht")
    public String listCVHT(Model model) {
        model.addAttribute("listCVHT", userService.getAllCVHT());
        return "admin/cvht/list";
    }

    @GetMapping("/cvht/create")
    public String formCreateCVHT(Model model) {
        model.addAttribute("cvht", new CVHTJpaEntity());
        model.addAttribute("isEdit", false);
        return "admin/cvht/form";
    }

    @GetMapping("/cvht/edit/{id}")
    public String formEditCVHT(@PathVariable String id, Model model) {
        CVHTJpaEntity cv = userService.getCVHTById(id);
        cv.setPassword("");
        model.addAttribute("cvht", cv);
        model.addAttribute("isEdit", true);
        return "admin/cvht/form";
    }

    @PostMapping("/cvht/save")
    public String saveCVHT(@ModelAttribute CVHTJpaEntity cv) {
        userService.saveCVHT(cv);
        return "redirect:/admin/cvht";
    }

    @GetMapping("/cvht/delete/{id}")
    public String deleteCVHT(@PathVariable String id) {
        userService.deleteCVHT(id);
        return "redirect:/admin/cvht";
    }
}
