package com.hoidap.hoidapdemo.repository.cvht;

import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CVHTJpaRepository extends JpaRepository<CVHTJpaEntity, String>{
    Optional<CVHTJpaEntity> findByEmail(String email);

    @Query("SELECT c FROM CVHTJpaEntity c WHERE LOWER(c.maCv) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<CVHTJpaEntity> searchCVHT(@org.springframework.data.repository.query.Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT MAX(c.maCv) FROM CVHTJpaEntity c WHERE c.maCv LIKE 'b%'")
    String findMaxMaCv();
}
