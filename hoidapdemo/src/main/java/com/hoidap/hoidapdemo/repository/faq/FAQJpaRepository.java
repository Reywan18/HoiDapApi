package com.hoidap.hoidapdemo.repository.faq;

import com.hoidap.hoidapdemo.entity.faq.FAQJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FAQJpaRepository extends JpaRepository<FAQJpaEntity, Long>, JpaSpecificationExecutor<FAQJpaEntity> {
}
