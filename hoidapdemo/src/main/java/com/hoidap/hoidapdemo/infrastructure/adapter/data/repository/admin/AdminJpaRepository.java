package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.admin;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.admin.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    Optional<AdminJpaEntity> findByEmail(String email);
}
