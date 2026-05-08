package com.hoidap.hoidapdemo.service;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.dto.auth.AccountCreatedResponse;
import com.hoidap.hoidapdemo.dto.auth.CreateUserRequest;
import com.hoidap.hoidapdemo.service.port.AdminServicePort;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminServicePort {
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

    @Transactional
    public AccountCreatedResponse resetStudentPassword(String maSv) {
        SinhVienJpaEntity sv = sinhVienRepo.findById(maSv)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên có mã: " + maSv));
        
        String rawPassword = generateRandomPassword();
        sv.setPassword(passwordEncoder.encode(rawPassword));
        sinhVienRepo.save(sv);

        return AccountCreatedResponse.builder()
                .email(sv.getEmail())
                .generatedPassword(rawPassword)
                .message("Đặt lại mật khẩu thành công!")
                .build();
    }

    @Transactional
    public AccountCreatedResponse resetAdvisorPassword(String maCv) {
        CVHTJpaEntity cvht = cvhtRepo.findById(maCv)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CVHT có mã: " + maCv));
        
        String rawPassword = generateRandomPassword();
        cvht.setPassword(passwordEncoder.encode(rawPassword));
        cvhtRepo.save(cvht);

        return AccountCreatedResponse.builder()
                .email(cvht.getEmail())
                .generatedPassword(rawPassword)
                .message("Đặt lại mật khẩu thành công!")
                .build();
    }
}
