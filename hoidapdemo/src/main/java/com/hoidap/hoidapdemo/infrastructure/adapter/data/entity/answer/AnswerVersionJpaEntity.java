package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.answer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "phien_ban_tra_loi")
@Data
@NoArgsConstructor
public class AnswerVersionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private AnswerJpaEntity answer;

    private int version;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    private LocalDateTime thoiGianTao;

    private String fileName;
    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;
}
