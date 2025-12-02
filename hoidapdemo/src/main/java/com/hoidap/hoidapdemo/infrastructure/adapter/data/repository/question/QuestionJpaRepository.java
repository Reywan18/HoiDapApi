package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.question;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.question.QuestionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionJpaRepository extends
        JpaRepository<QuestionJpaEntity, Long>,
        JpaSpecificationExecutor<QuestionJpaEntity> {
    List<QuestionJpaEntity> findBySinhVien_MaSvOrderByNgayGuiDesc(String maSv);

    List<QuestionJpaEntity> findByCvht_MaCvOrderByNgayGuiDesc(String maCv);
    List<QuestionJpaEntity> findBySinhVien_EmailOrderByNgayGuiDesc(String email);
    List<QuestionJpaEntity> findByCvht_EmailOrderByNgayGuiDesc(String email);
}
