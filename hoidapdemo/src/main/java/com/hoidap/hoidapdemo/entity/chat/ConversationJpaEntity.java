package com.hoidap.hoidapdemo.entity.chat;

import com.hoidap.hoidapdemo.entity.enums.ConversationStatus;
import com.hoidap.hoidapdemo.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.entity.sinhvien.SinhVienJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
@Data
@NoArgsConstructor
public class ConversationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", referencedColumnName = "ma_sv", nullable = false)
    private SinhVienJpaEntity sinhVien;

    // Có thể null nếu chưa được CVHT tiếp nhận (vẫn đang chat với Bot)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cv", referencedColumnName = "ma_cv")
    private CVHTJpaEntity cvht;

    @Column(name = "tieu_de", length = 255)
    private String tieuDe;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 50)
    private ConversationStatus trangThai;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat_cuoi")
    private LocalDateTime ngayCapNhatCuoi;
}
