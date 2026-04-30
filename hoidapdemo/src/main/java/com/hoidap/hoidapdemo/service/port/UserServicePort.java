package com.hoidap.hoidapdemo.service.port;

import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.dto.user.ProfileUpdateRequest;
import com.hoidap.hoidapdemo.dto.user.UserDto;

import com.hoidap.hoidapdemo.dto.user.UserProfileResponse;

import java.util.List;

public interface UserServicePort {
    // String register(String email, String password, String hoTen, String
    // soDienThoai,UserRole role);

    String login(String email, String password);

    UserDto getUserByEmail(String email);

    UserProfileResponse getMyProfile(String email);

    void updateProfile(String email, ProfileUpdateRequest request);

    List<SinhVienJpaEntity> getAllSinhVien();

    org.springframework.data.domain.Page<SinhVienJpaEntity> getAllSinhVien(String keyword,
            org.springframework.data.domain.Pageable pageable);

    SinhVienJpaEntity getSinhVienById(String id);

    void saveSinhVien(SinhVienJpaEntity sv);

    void deleteSinhVien(String id);

    List<CVHTJpaEntity> getAllCVHT();

    org.springframework.data.domain.Page<CVHTJpaEntity> getAllCVHT(String keyword,
            org.springframework.data.domain.Pageable pageable);

    CVHTJpaEntity getCVHTById(String id);

    void saveCVHT(CVHTJpaEntity cvht);

    void deleteCVHT(String id);
}
