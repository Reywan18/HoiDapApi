package com.hoidap.hoidapdemo.repository.lop;

import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LopJpaRepository extends JpaRepository<LopJpaEntity, String> {
    @Query("SELECT l FROM LopJpaEntity l WHERE LOWER(l.maLop) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.chuyenNganh) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LopJpaEntity> searchLop(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT l FROM LopJpaEntity l WHERE TRIM(LOWER(l.cvht.maCv)) = TRIM(LOWER(:maCv))")
    java.util.List<LopJpaEntity> findByCvhtId(@Param("maCv") String maCv);
}
