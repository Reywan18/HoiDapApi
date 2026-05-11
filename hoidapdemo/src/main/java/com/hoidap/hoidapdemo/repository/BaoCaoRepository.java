package com.hoidap.hoidapdemo.repository;

import com.hoidap.hoidapdemo.entity.report.BaoCaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaoCaoRepository extends JpaRepository<BaoCaoJpaEntity, Long> {
}
