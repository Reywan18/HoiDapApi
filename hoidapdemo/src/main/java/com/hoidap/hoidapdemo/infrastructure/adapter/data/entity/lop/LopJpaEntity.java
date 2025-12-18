package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.lop;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.sinhvien.SinhVienJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
    @ToString.Exclude
    private CVHTJpaEntity cvht;

    @OneToMany(mappedBy = "lop")
    @ToString.Exclude
    private List<SinhVienJpaEntity> sinhViens;
}
