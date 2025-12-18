package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository.faq;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.faq.FAQJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FAQJpaRepository extends JpaRepository<FAQJpaEntity, Long>, JpaSpecificationExecutor<FAQJpaEntity> {
}
