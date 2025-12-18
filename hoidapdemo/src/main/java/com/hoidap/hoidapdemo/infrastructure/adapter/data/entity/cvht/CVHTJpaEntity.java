package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.cvht;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.lop.LopJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "cvht")
@Data
@NoArgsConstructor
public class CVHTJpaEntity {
    @Id
    @Column(name = "ma_cv", unique = true, nullable = false, length = 10)
    private String maCv;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "chuyen_mon")
    private String chuyenMon;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "cvht", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LopJpaEntity> cacLopPhuTrach;

}
