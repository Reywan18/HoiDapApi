package com.hoidap.hoidapdemo.entity.faq;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoidap.hoidapdemo.entity.admin.AdminJpaEntity;

@Entity
@Table(name = "faq")
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

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminJpaEntity admin;
}
