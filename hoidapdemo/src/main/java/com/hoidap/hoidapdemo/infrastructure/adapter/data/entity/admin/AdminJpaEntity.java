package com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.admin;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="admin")
@Data
@NoArgsConstructor
public class AdminJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String hoTen;
}
