package com.hoidap.hoidapdemo.application.port;

import com.hoidap.hoidapdemo.domain.model.UserRole;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.UserDto;

import java.util.List;

public interface UserServicePort {
    String register(String email, String password, String hoTen, String soDienThoai,UserRole role);

    String login(String email, String password);

    UserDto getUserByEmail(String email);

    void updateProfile(String email, String maLop, String chuyenMon);

    List<SinhVienJpaEntity> getAllSinhVien();
    SinhVienJpaEntity getSinhVienById(String id);
    void saveSinhVien(SinhVienJpaEntity sv);
    void deleteSinhVien(String id);

    List<CVHTJpaEntity> getAllCVHT();
    CVHTJpaEntity getCVHTById(String id);
    void saveCVHT(CVHTJpaEntity cvht);
    void deleteCVHT(String id);
}
