package com.hoidap.hoidapdemo.security;

import com.hoidap.hoidapdemo.entity.enums.UserRole;
import com.hoidap.hoidapdemo.entity.admin.AdminJpaEntity;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.repository.admin.AdminJpaRepository;
import com.hoidap.hoidapdemo.repository.cvht.CVHTJpaRepository;
import com.hoidap.hoidapdemo.repository.sinhvien.SinhVienJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerUserDetailService implements  UserDetailsService {
    private final SinhVienJpaRepository sinhVienRepo;
    private final CVHTJpaRepository cvhtRepo;
    private final AdminJpaRepository adminRepo;

    public CustomerUserDetailService(SinhVienJpaRepository sinhVienRepo, CVHTJpaRepository cvhtRepo, AdminJpaRepository adminRepo) {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<SinhVienJpaEntity> svOptional = sinhVienRepo.findByEmail(email);
        if (svOptional.isPresent()) {
            SinhVienJpaEntity sv = svOptional.get();
            return User.builder()
                        .username(sv.getEmail())
                        .password(sv.getPassword())
                        .roles(UserRole.SINH_VIEN.name())
                        .build();
        }

        Optional<CVHTJpaEntity> cvhtOptional = cvhtRepo.findByEmail(email);
        if (cvhtOptional.isPresent()) {
            CVHTJpaEntity cv = cvhtOptional.get();
            return User.builder()
                        .username(cv.getEmail())
                        .password(cv.getPassword())
                        .roles(UserRole.CVHT.name())
                        .build();
        }

        var adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            AdminJpaEntity admin = adminOpt.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .roles(UserRole.ADMIN.name())
                    .build();
        }

        throw  new UsernameNotFoundException("User not found with email: " + email);
    }
}
