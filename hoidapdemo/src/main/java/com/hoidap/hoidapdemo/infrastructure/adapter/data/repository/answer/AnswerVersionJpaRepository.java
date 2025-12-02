package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.answer;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.answer.AnswerVersionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerVersionJpaRepository extends JpaRepository<AnswerVersionJpaEntity, Long> {
    List<AnswerVersionJpaEntity> findByAnswer_IdOrderByVersionDesc(Long answerId);
}
