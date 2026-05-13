package com.hoidap.hoidapdemo.repository;

import com.hoidap.hoidapdemo.entity.report.ReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportJpaEntity, Long> {
}
