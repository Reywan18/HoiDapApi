package com.hoidap.hoidapdemo.repository.cvht;

import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CVHTJpaRepository extends JpaRepository<CVHTJpaEntity, String>{
    Optional<CVHTJpaEntity> findByEmail(String email);

    @Query("SELECT MAX(c.maCv) FROM CVHTJpaEntity c WHERE c.maCv LIKE 'b%'")
    String findMaxMaCv();
}
