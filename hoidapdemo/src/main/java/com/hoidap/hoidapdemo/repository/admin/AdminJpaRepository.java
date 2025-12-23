package com.hoidap.hoidapdemo.repository.admin;

import com.hoidap.hoidapdemo.entity.admin.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    Optional<AdminJpaEntity> findByEmail(String email);
}
