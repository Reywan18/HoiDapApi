package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    Optional<AdminJpaEntity> findByEmail(String email);
}
