package com.gatto.rms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Resource {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private String countryCode;

    @OneToOne(cascade = CascadeType.ALL)
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resource")
    private List<Characteristic> characteristics;
}
