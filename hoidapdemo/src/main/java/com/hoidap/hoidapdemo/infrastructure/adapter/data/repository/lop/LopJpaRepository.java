package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.lop;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.lop.LopJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LopJpaRepository extends JpaRepository<LopJpaEntity, String> {
}
