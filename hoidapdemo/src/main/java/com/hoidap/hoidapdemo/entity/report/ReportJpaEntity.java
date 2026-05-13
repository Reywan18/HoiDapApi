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
public class ReportJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private com.hoidap.hoidapdemo.entity.chat.ConversationJpaEntity conversation;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Column(name = "thoi_gian_bao_cao")
    private LocalDateTime thoiGianBaoCao;

    @Column(name = "trang_thai")
    private String trangThai; // "PENDING", "RESOLVED"
}
