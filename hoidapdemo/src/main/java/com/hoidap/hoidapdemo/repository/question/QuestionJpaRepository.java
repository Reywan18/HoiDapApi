package com.hoidap.hoidapdemo.repository.question;

import com.hoidap.hoidapdemo.entity.enums.QuestionStatus;
import com.hoidap.hoidapdemo.entity.question.QuestionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface QuestionJpaRepository extends
        JpaRepository<QuestionJpaEntity, Long>,
        JpaSpecificationExecutor<QuestionJpaEntity> {
    List<QuestionJpaEntity> findBySinhVien_MaSvOrderByNgayGuiDesc(String maSv);

    List<QuestionJpaEntity> findByCvht_MaCvOrderByNgayGuiDesc(String maCv);
    List<QuestionJpaEntity> findBySinhVien_EmailOrderByNgayGuiDesc(String email);
    List<QuestionJpaEntity> findByCvht_EmailOrderByNgayGuiDesc(String email);

    long count();
    long countByTrangThai(QuestionStatus status);

    @Query("SELECT q.sinhVien.hoTen, q.sinhVien.maSv, COUNT(q) as cnt " +
            "FROM QuestionJpaEntity q " +
            "GROUP BY q.sinhVien.maSv, q.sinhVien.hoTen " +
            "ORDER BY cnt DESC")
    List<Object[]> findTopStudents(Pageable pageable);

    @Query(value = """
        SELECT 
            c.ho_ten AS name,
            AVG(TIMESTAMPDIFF(HOUR, q.ngaygui, v.thoi_gian_tao)) AS avgTime 
        FROM cau_hoi q
        JOIN cau_tra_loi a ON q.ma_cau_hoi = a.ma_cau_hoi
        JOIN phien_ban_tra_loi v ON a.id = v.answer_id
        JOIN cvht c ON a.ma_cv = c.ma_cv
        WHERE v.version = 1 
        GROUP BY c.ma_cv, c.ho_ten
        ORDER BY avgTime ASC
        """, nativeQuery = true)
    List<Object[]> findAdvisorPerformance();

    @Query("SELECT l.maLop, l.chuyenNganh, COUNT(q) as cnt " +
            "FROM QuestionJpaEntity q " +
            "JOIN q.sinhVien s " +
            "JOIN s.lop l " +
            "GROUP BY l.maLop, l.chuyenNganh " +
            "ORDER BY cnt DESC")
    List<Object[]> countQuestionsByClass();
}
