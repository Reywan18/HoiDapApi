package com.hoidap.hoidapdemo.entity.sinhvien;

import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(name = "role")
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_lop", referencedColumnName = "ma_lop")
    @ToString.Exclude
    private LopJpaEntity lop;
}
