package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity;

import com.hoidap.hoidapdemo.domain.model.QuestionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cau_hoi")
@Data
@NoArgsConstructor
public class QuestionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cau_hoi")
    private Long maCauHoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ma_sv",
            referencedColumnName = "ma_sv",
            nullable = false
    )
    private SinhVienJpaEntity sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ma_cv",
            referencedColumnName = "ma_cv",
            nullable = false
    )
    private CVHTJpaEntity cvht;

    @Column(name = "tieude", length = 255)
    private String tieuDe;

    @Column(name = "noidung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ngaygui", nullable = false)
    private LocalDateTime ngayGui;

    @Column(name = "ngaycapnhatcuoi")
    private LocalDateTime ngayCapNhatCuoi;

    @Convert(converter = QuestionStatusConverter.class)
    @Column(name = "trangthai")
    private QuestionStatus trangThai;
}
