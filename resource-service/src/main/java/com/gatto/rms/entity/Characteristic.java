package com.gatto.rms.entity;

import com.gatto.rms.contracts.CharacteristicType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
