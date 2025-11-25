package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lop")
@Data
@NoArgsConstructor
public class LopJpaEntity {
    @Id
    @Column(name = "ma_lop", length = 10, nullable = false)
    private String maLop;

    @Column(name = "khoa_hoc")
    private String khoaHoc;

    @Column(name = "chuyen_nganh")
    private String chuyenNganh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cvht", referencedColumnName = "ma_cv")
    private CVHTJpaEntity cvht;
}
