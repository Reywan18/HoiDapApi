package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.sinhvien;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.sinhvien.SinhVienJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SinhVienJpaRepository extends JpaRepository<SinhVienJpaEntity, String>{
    Optional<SinhVienJpaEntity> findByEmail(String email);

    @Query("SELECT MAX(s.maSv) FROM SinhVienJpaEntity s WHERE s.maSv LIKE 'a%'")
    String findMaxMaSv();
}
