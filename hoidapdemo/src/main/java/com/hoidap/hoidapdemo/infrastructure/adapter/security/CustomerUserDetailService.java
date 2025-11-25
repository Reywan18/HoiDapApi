package com.hoidap.hoidapdemo.infrastructure.adapter.security;

import com.hoidap.hoidapdemo.domain.model.UserRole;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.CVHTJpaRepository;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.SinhVienJpaRepository;
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

    public CustomerUserDetailService(SinhVienJpaRepository sinhVienRepo, CVHTJpaRepository cvhtRepo) {
        this.sinhVienRepo = sinhVienRepo;
        this.cvhtRepo = cvhtRepo;
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

        throw  new UsernameNotFoundException("User not found with email: " + email);
    }
}
