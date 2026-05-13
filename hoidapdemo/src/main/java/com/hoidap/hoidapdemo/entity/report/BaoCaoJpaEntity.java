package com.hoidap.hoidapdemo.entity.report;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bao_cao_vi_pham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Column(name = "thoi_gian_bao_cao")
    private LocalDateTime thoiGianBaoCao;

    @Column(name = "trang_thai")
    private String trangThai; // "PENDING", "RESOLVED"
}
