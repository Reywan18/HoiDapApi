package com.hoidap.hoidapdemo.service.port;

import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.dto.user.ProfileUpdateRequest;
import com.hoidap.hoidapdemo.dto.user.UserDto;

import java.util.List;

public interface UserServicePort {
//    String register(String email, String password, String hoTen, String soDienThoai,UserRole role);

    String login(String email, String password);

    UserDto getUserByEmail(String email);

    void updateProfile(String email, ProfileUpdateRequest request);

    List<SinhVienJpaEntity> getAllSinhVien();
    SinhVienJpaEntity getSinhVienById(String id);
    void saveSinhVien(SinhVienJpaEntity sv);
    void deleteSinhVien(String id);

    List<CVHTJpaEntity> getAllCVHT();
    CVHTJpaEntity getCVHTById(String id);
    void saveCVHT(CVHTJpaEntity cvht);
    void deleteCVHT(String id);
}
