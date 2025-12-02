package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.answer;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.answer.AnswerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerJpaRepository extends JpaRepository<AnswerJpaEntity, Long> {
    Optional<AnswerJpaEntity> findByQuestion_MaCauHoi(Long questionId);
}
