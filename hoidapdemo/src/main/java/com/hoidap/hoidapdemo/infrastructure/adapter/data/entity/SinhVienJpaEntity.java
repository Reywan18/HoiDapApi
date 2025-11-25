package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sinh_vien")
@Data
@NoArgsConstructor
public class SinhVienJpaEntity {
    @Id
    @Column(name = "ma_sv", unique = true, nullable = false, length = 10)
    private String maSv;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lop", referencedColumnName = "ma_lop")
    private LopJpaEntity lop;
}
