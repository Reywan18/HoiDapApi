package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.answer;

import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.cvht.CVHTJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.question.QuestionJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cau_tra_loi")
@Data
@NoArgsConstructor
public class AnswerJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cau_hoi")
    private QuestionJpaEntity question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cv")
    private CVHTJpaEntity cvht;

    @Column(name = "phien_ban_hien_tai")
    private int currentVersion = 0;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<AnswerVersionJpaEntity> versions;
}
