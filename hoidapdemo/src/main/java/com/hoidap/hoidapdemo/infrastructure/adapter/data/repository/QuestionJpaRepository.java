package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.QuestionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long>{
}
