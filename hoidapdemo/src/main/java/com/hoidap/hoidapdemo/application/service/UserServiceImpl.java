package com.hoidap.hoidapdemo.application.service;

import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.domain.model.UserRole;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.LopJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.LopJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.security.JwtUtils;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServicePort {
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final LopJpaRepository lopRepo;

    public UserServiceImpl(
            SinhVienJpaRepository sinhVienRepo,
            CVHTJpaRepository cvhtRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            LopJpaRepository lopRepo)
    {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.lopRepo = lopRepo;
    }

    @Override
    @Transactional
    public String register(String email, String password, String hoTen, String soDienThoai,UserRole role) {
        if (sinhVienRepo.findByEmail(email).isPresent() || cvhtRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);
        String generatedId;

        if (role == UserRole.SINH_VIEN) {
            String maxSvId = sinhVienRepo.findMaxMaSv();
            generatedId = generateNextId(maxSvId, "a");

            SinhVienJpaEntity sv = new SinhVienJpaEntity();
            sv.setMaSv(generatedId);
            sv.setEmail(email);
            sv.setPassword(hashedPassword);
            sv.setHoTen(hoTen);
            sv.setSoDienThoai(soDienThoai);

            sinhVienRepo.save(sv);
            return sv.getMaSv();

        } else if (role == UserRole.CVHT) {
            String maxCvId = cvhtRepo.findMaxMaCv();
            generatedId = generateNextId(maxCvId, "b");

            CVHTJpaEntity cv = new CVHTJpaEntity();
            cv.setMaCv(generatedId);
            cv.setEmail(email);
            cv.setPassword(hashedPassword);
            cv.setHoTen(hoTen);
            cv.setSoDienThoai(soDienThoai);
            cv.setChuyenMon(null);

            cvhtRepo.save(cv);
            return cv.getMaCv();
        }

        throw new IllegalArgumentException("Invalid user role specified: " + role.name());
    }

    @Override
    public String login(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
             new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDto userDetails = this.getUserByEmail(email);

        return jwtUtils.generateJwtToken(
                authentication,
                userDetails.getMaDinhDanh(),
                userDetails.getHoTen(),
                userDetails.getRole()
        );
    }

    @Override
    public UserDto getUserByEmail(String email) {
        var svOpt = sinhVienRepo.findByEmail(email);
        if (svOpt.isPresent()) {
            SinhVienJpaEntity sv = svOpt.get();
            return UserDto.builder()
                    .maDinhDanh(sv.getMaSv())
                    .hoTen(sv.getHoTen())
                    .email(sv.getEmail())
                    .role(UserRole.SINH_VIEN.name())
                    .build();
        }

        var cvhtOpt = cvhtRepo.findByEmail(email);
        if (cvhtOpt.isPresent()) {
            CVHTJpaEntity cv = cvhtOpt.get();
            return UserDto.builder()
                    .maDinhDanh(cv.getMaCv())
                    .hoTen(cv.getHoTen())
                    .email(cv.getEmail())
                    .role(UserRole.CVHT.name())
                    .build();
        }

        throw new IllegalArgumentException("User not found with email: " + email);
    }

    private String generateNextId(String maxId, String prefix) {
        if (maxId == null || maxId.length() < 6) {
            return prefix + "00000";
        }

        String numberPart = maxId.substring(1);
        int number = Integer.parseInt(numberPart);
        int nextNumber = number + 1;

        String nextNumberPart = String.format("%05d", nextNumber);

        return prefix + nextNumberPart;
    }

    @Override
    @Transactional
    public void updateProfile(String email, String maLop, String chuyenMon) {
        var svOptional = sinhVienRepo.findByEmail(email);
        if (svOptional.isPresent()) {
            if (maLop == null) throw new IllegalArgumentException("Vui lòng cung cấp mã lớp.");

            SinhVienJpaEntity sv = svOptional.get();

            LopJpaEntity lop = lopRepo.findById(maLop)
                    .orElseThrow(() -> new IllegalArgumentException("Mã lớp không tồn tại: " + maLop));

            sv.setLop(lop);
            sinhVienRepo.save(sv);
            return;
        }

        var cvhtOptional = cvhtRepo.findByEmail(email);
        if (cvhtOptional.isPresent()) {
            if (chuyenMon == null) throw new IllegalArgumentException("Vui lòng cung cấp chuyên môn.");

            CVHTJpaEntity cvht = cvhtOptional.get();

            cvht.setChuyenMon(chuyenMon);
            cvhtRepo.save(cvht);
            return;
        }

        throw new UsernameNotFoundException("Không tìm thấy người dùng.");
    }
}
