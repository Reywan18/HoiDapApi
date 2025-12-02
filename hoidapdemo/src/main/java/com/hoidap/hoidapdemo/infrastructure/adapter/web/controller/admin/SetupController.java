package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.admin;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.admin.AdminJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.admin.AdminJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
public class SetupController {
    private final AdminJpaRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    public SetupController(AdminJpaRepository adminRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-admin")
    public String createAdmin(@RequestBody LoginRequest request) {
        if (adminRepo.findByEmail(request.getEmail()).isPresent()) {
            return "Admin đã tồn tại!";
        }

        AdminJpaEntity admin = new AdminJpaEntity();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setHoTen("Super Administrator");

        adminRepo.save(admin);
        return "Tạo Admin thành công: " + request.getEmail();
    }
}
