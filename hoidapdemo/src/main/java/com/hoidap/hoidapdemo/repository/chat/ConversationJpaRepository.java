package com.hoidap.hoidapdemo.repository.chat;

import com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity;
import com.hoidap.hoidapdemo.entity.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationJpaRepository
        extends JpaRepository<ConversationJpaEntity, Long>, JpaSpecificationExecutor<ConversationJpaEntity> {

    // Tìm các cuộc hội thoại của 1 sinh viên cấp cụ thể (Loại trừ Bot)
    Page<ConversationJpaEntity> findBySinhVien_MaSvAndTrangThaiNot(String maSv, ConversationStatus trangThai, Pageable pageable);

    // Tìm các cuộc hội thoại đang được một CVHT cụ thể đảm nhận (Loại trừ Bot)
    Page<ConversationJpaEntity> findByCvht_MaCvAndTrangThaiNot(String maCv, ConversationStatus trangThai, Pageable pageable);

    long countByTrangThai(ConversationStatus trangThai);

    @Query("SELECT c.sinhVien.hoTen, c.sinhVien.maSv, COUNT(c) FROM ConversationJpaEntity c GROUP BY c.sinhVien.hoTen, c.sinhVien.maSv ORDER BY COUNT(c) DESC")
    List<Object[]> findTopStudents(Pageable pageable);

    @Query(value = "SELECT " +
           "  c.ho_ten, " +
           "  AVG(TIMESTAMPDIFF(HOUR, conv.ngay_tao, conv.ngay_cap_nhat_cuoi)) as avg_time, " +
           "  COUNT(CASE WHEN conv.trang_thai = 'RESOLVED' THEN 1 END) as answered_count, " +
           "  (SELECT COUNT(*) FROM conversation conv2 " +
           "   JOIN sinh_vien sv2 ON conv2.ma_sv = sv2.ma_sv " +
           "   JOIN lop l2 ON sv2.ma_lop = l2.ma_lop " +
           "   WHERE l2.ma_cvht = c.ma_cv) as total_questions " +
           "FROM cvht c " +
           "LEFT JOIN conversation conv ON conv.ma_cv = c.ma_cv " +
           "GROUP BY c.ma_cv, c.ho_ten", nativeQuery = true)
    List<Object[]> findAdvisorPerformance();

    @Query("SELECT c.sinhVien.lop.maLop, c.sinhVien.lop.chuyenNganh, COUNT(c) FROM ConversationJpaEntity c GROUP BY c.sinhVien.lop.maLop, c.sinhVien.lop.chuyenNganh")
    List<Object[]> countQuestionsByClass();

    @Query("SELECT c FROM ConversationJpaEntity c WHERE " +
           "LOWER(c.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.sinhVien.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.cvht.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ConversationJpaEntity> searchAllConversations(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}
