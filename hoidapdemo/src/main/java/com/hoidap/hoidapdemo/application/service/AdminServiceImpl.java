package com.hoidap.hoidapdemo.application.service;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.sinhvien.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.AccountCreatedResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.auth.CreateUserRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminServiceImpl {
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(
            SinhVienJpaRepository sinhVienRepo,
            CVHTJpaRepository cvhtRepo,
            PasswordEncoder passwordEncoder)
    {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Transactional
    public AccountCreatedResponse createStudentAccount(CreateUserRequest request) {
        String maSv = request.getMaDinhDanh().trim().toUpperCase();
        String email = request.getEmail().trim().toLowerCase();

        String expectedEmail = maSv.toLowerCase() + "@thanglong.edu.vn";
        if (!email.equals(expectedEmail)) {
            throw new IllegalArgumentException("Email sinh viên phải có dạng: " + expectedEmail);
        }

        if (sinhVienRepo.existsById(maSv)) {
            throw new IllegalArgumentException("Mã sinh viên " + maSv + " đã tồn tại!");
        }
        if (sinhVienRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email " + email + " đã được sử dụng!");
        }

        String rawPassword = generateRandomPassword();

        SinhVienJpaEntity sv = new SinhVienJpaEntity();
        sv.setMaSv(maSv);
        sv.setEmail(email);
        sv.setPassword(passwordEncoder.encode(rawPassword));
        sv.setRole("SINH_VIEN");
        sv.setHoTen("Chưa cập nhật");

        sinhVienRepo.save(sv);

        return AccountCreatedResponse.builder()
                .email(email)
                .generatedPassword(rawPassword)
                .message("Tạo thành công. Mã SV: " + maSv)
                .build();
    }

    @Transactional
    public AccountCreatedResponse createAdvisorAccount(CreateUserRequest request) {
        String maCv = request.getMaDinhDanh().trim().toUpperCase();
        String email = request.getEmail().trim().toLowerCase();

        String expectedEmail = maCv.toLowerCase() + "@thanglong.edu.vn";
        if (!email.equals(expectedEmail)) {
            throw new IllegalArgumentException("Email CVHT phải có dạng: " + expectedEmail);
        }

        if (cvhtRepo.existsById(maCv)) {
            throw new IllegalArgumentException("Mã CVHT " + maCv + " đã tồn tại!");
        }
        if (cvhtRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email " + email + " đã được sử dụng!");
        }

        String rawPassword = generateRandomPassword();

        CVHTJpaEntity cvht = new CVHTJpaEntity();
        cvht.setMaCv(maCv);
        cvht.setEmail(email);
        cvht.setPassword(passwordEncoder.encode(rawPassword));
        cvht.setRole("CVHT");
        cvht.setHoTen("Chưa cập nhật");

        cvhtRepo.save(cvht);

        return AccountCreatedResponse.builder()
                .email(email)
                .generatedPassword(rawPassword)
                .message("Tạo thành công. Mã CVHT: " + maCv)
                .build();
    }
}
