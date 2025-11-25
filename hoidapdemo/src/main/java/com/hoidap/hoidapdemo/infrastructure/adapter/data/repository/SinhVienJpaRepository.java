package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.SinhVienJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SinhVienJpaRepository extends JpaRepository<SinhVienJpaEntity, String>{
    Optional<SinhVienJpaEntity> findByEmail(String email);
}
