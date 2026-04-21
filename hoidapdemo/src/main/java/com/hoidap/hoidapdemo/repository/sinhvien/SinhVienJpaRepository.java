package com.hoidap.hoidapdemo.repository.sinhvien;

import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Repository
public interface SinhVienJpaRepository extends JpaRepository<SinhVienJpaEntity, String>{
    Optional<SinhVienJpaEntity> findByEmail(String email);

    @Query("SELECT MAX(s.maSv) FROM SinhVienJpaEntity s WHERE s.maSv LIKE 'a%'")
    String findMaxMaSv();

    @Query("SELECT s FROM SinhVienJpaEntity s WHERE LOWER(s.maSv) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SinhVienJpaEntity> searchSinhVien(@Param("keyword") String keyword, Pageable pageable);
}
