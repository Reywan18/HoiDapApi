package com.hoidap.hoidapdemo.controller.api.auth;

import com.hoidap.hoidapdemo.entity.admin.AdminJpaEntity;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import com.hoidap.hoidapdemo.repository.admin.AdminJpaRepository;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.repository.lop.LopJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    private final AdminJpaRepository adminRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final SinhVienJpaRepository sinhVienRepo;
    private final LopJpaRepository lopRepo;
    private final PasswordEncoder passwordEncoder;

    public SetupController(AdminJpaRepository adminRepo, CVHTJpaRepository cvhtRepo,
                           SinhVienJpaRepository sinhVienRepo, LopJpaRepository lopRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.cvhtRepo = cvhtRepo;
        this.sinhVienRepo = sinhVienRepo;
        this.lopRepo = lopRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Email va password khong duoc de trong!");
        }
        if (adminRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body("Admin da ton tai voi email: " + email);
        }

        AdminJpaEntity admin = new AdminJpaEntity();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setHoTen("Administrator");
        adminRepo.save(admin);

        return ResponseEntity.ok("Tao Admin thanh cong! Email: " + email);
    }

    @PostMapping("/create-cvht")
    public ResponseEntity<String> createCvht(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String hoTen = body.getOrDefault("hoTen", "Co Van Hoc Tap");
        String soDienThoai = body.getOrDefault("soDienThoai", "0000000000");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Email va password khong duoc de trong!");
        }
        if (cvhtRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body("CVHT da ton tai voi email: " + email);
        }

        // Tao ma_cv tu dong
        String maxId = cvhtRepo.findMaxMaCv();
        String maCv = generateNextId(maxId, "b");

        CVHTJpaEntity cv = new CVHTJpaEntity();
        cv.setMaCv(maCv);
        cv.setEmail(email);
        cv.setPassword(passwordEncoder.encode(password));
        cv.setHoTen(hoTen);
        cv.setSoDienThoai(soDienThoai);
        cv.setRole("CVHT");
        cvhtRepo.save(cv);

        return ResponseEntity.ok("Tao CVHT thanh cong! Ma: " + maCv + " | Email: " + email);
    }

    @PostMapping("/create-student")
    public ResponseEntity<String> createStudent(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String hoTen = body.getOrDefault("hoTen", "Sinh Vien");
        String soDienThoai = body.getOrDefault("soDienThoai", "0000000000");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Email va password khong duoc de trong!");
        }
        if (sinhVienRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body("Sinh vien da ton tai voi email: " + email);
        }

        // Tao ma_sv tu dong
        String maxId = sinhVienRepo.findMaxMaSv();
        String maSv = generateNextId(maxId, "a");

        SinhVienJpaEntity sv = new SinhVienJpaEntity();
        sv.setMaSv(maSv);
        sv.setEmail(email);
        sv.setPassword(passwordEncoder.encode(password));
        sv.setHoTen(hoTen);
        sv.setSoDienThoai(soDienThoai);
        sv.setRole("SINH_VIEN");
        sinhVienRepo.save(sv);

        return ResponseEntity.ok("Tao Sinh Vien thanh cong! Ma: " + maSv + " | Email: " + email);
    }

    @PostMapping("/link-demo-data")
    public ResponseEntity<String> linkDemoData() {
        try {
            // Find default accounts
            var svOpt = sinhVienRepo.findByEmail("sinhvien@thanglong.edu.vn");
            var cvhtOpt = cvhtRepo.findByEmail("covan@thanglong.edu.vn");

            if (svOpt.isEmpty() || cvhtOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Loi: Chua co tai khoan sinhvien hoac covan. Hay chay create-student va create-cvht truoc.");
            }

            SinhVienJpaEntity sv = svOpt.get();
            CVHTJpaEntity cv = cvhtOpt.get();

            // Create or update Demo Class
            String maLop = "TT34CL01";
            LopJpaEntity lop = lopRepo.findById(maLop).orElseGet(() -> {
                LopJpaEntity newLop = new LopJpaEntity();
                newLop.setMaLop(maLop);
                newLop.setChuyenNganh("Công nghệ thông tin");
                newLop.setKhoaHoc("K34");
                return newLop;
            });

            // Assign Advisor to Class
            lop.setCvht(cv);
            lopRepo.save(lop);

            // Assign Class to Student
            sv.setLop(lop);
            sinhVienRepo.save(sv);

            return ResponseEntity.ok("Setup thanh cong! Sinh vien " + sv.getMaSv() + " da duoc gan vao lop " + maLop + " do CVHT " + cv.getMaCv() + " quan ly.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Loi setup: " + e.getMessage());
        }
    }

    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email) {
        var svOpt = sinhVienRepo.findByEmail(email);
        if (svOpt.isEmpty()) return ResponseEntity.notFound().build();
        
        SinhVienJpaEntity sv = svOpt.get();
        Map<String, Object> details = new java.util.HashMap<>();
        details.put("maSv", sv.getMaSv());
        details.put("hoTen", sv.getHoTen());
        details.put("email", sv.getEmail());
        details.put("lop", sv.getLop() != null ? sv.getLop().getMaLop() : "null");
        if (sv.getLop() != null) {
            details.put("cvht", sv.getLop().getCvht() != null ? sv.getLop().getCvht().getMaCv() : "null");
            details.put("tenCvht", sv.getLop().getCvht() != null ? sv.getLop().getCvht().getHoTen() : "null");
        }
        
        return ResponseEntity.ok(details);
    }

    private String generateNextId(String maxId, String prefix) {
        if (maxId == null || maxId.length() < 6) {
            return prefix + "00001";
        }
        String numberPart = maxId.substring(1);
        int number = Integer.parseInt(numberPart);
        return prefix + String.format("%05d", number + 1);
    }
}

