package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.LopJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopJpaRepository extends JpaRepository<LopJpaEntity, String> {
}
