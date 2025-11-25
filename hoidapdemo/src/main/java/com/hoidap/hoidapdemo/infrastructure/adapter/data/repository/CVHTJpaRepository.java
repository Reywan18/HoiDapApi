package com.hoidap.hoidapdemo.infrastructure.adapter.data.repository;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.CVHTJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CVHTJpaRepository extends JpaRepository<CVHTJpaEntity, String>{
    Optional<CVHTJpaEntity> findByEmail(String email);
}
