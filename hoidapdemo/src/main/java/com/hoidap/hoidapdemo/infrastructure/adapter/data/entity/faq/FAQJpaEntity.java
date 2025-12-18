package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.faq;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="faq")
@Data
public class FAQJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maFaq; // Tự tăng

    private String chuDe;
    private String tieuDe;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    private String khoaVien;
    private String khoaHoc;
    private String namHoc;
}
