package com.hoidap.hoidapdemo.entity.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoidap.hoidapdemo.entity.faq.FAQJpaEntity;
import java.util.List;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
public class AdminJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String hoTen;

    @JsonIgnore
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FAQJpaEntity> faqs;
}
