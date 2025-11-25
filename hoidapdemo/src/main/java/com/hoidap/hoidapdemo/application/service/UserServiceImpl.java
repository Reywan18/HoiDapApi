package com.hoidap.hoidapdemo.application.service;

import com.hoidap.hoidapdemo.application.port.UserServicePort;
import com.hoidap.hoidapdemo.domain.model.UserRole;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.SinhVienJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.security.JwtUtils;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.UserDto;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserServicePort {
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(
            SinhVienJpaRepository sinhVienRepo,
            CVHTJpaRepository cvhtRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils)
    {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public String register(String email, String password, String maDinhDanh, String hoTen, String soDienThoai,UserRole role) {
        if (sinhVienRepo.findByEmail(email).isPresent() || cvhtRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        if (role == UserRole.SINH_VIEN) {
            if (sinhVienRepo.existsById(maDinhDanh)) {
                throw new IllegalArgumentException("MaSV alreadt exists");
            }

            SinhVienJpaEntity sv = new SinhVienJpaEntity();
            sv.setMaSv(maDinhDanh);
            sv.setEmail(email);
            sv.setPassword(hashedPassword);
            sv.setHoTen(hoTen);
            sv.setSoDienThoai(soDienThoai);

            sinhVienRepo.save(sv);
            return sv.getMaSv();

        } else if (role == UserRole.CVHT) {
            if (cvhtRepo.existsById(maDinhDanh)) {
                throw new IllegalArgumentException("MaCV alreadt exists");
            }

            CVHTJpaEntity cv = new CVHTJpaEntity();
            cv.setMaCv(maDinhDanh);
            cv.setEmail(email);
            cv.setPassword(hashedPassword);
            cv.setHoTen(hoTen);
            cv.setSoDienThoai(soDienThoai);

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
}
