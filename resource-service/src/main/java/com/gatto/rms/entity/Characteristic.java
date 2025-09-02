package com.gatto.rms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "characteristic")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Characteristic {
    @Id @GeneratedValue
    private Long id;

    @Column(length = 5)
    private String code;

    @Enumerated(EnumType.STRING)
    private CharacteristicType type;

    private String value;

    @ToString.Exclude
    @ManyToOne
    private Resource resource;
}
