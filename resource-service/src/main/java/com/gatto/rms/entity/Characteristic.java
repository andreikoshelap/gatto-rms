package com.gatto.rms.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "characteristic")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Characteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private CharacteristicType type;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}
