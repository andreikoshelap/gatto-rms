package com.gatto.rms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "resource")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ResourceType type;

    @Column(length = 2, nullable = false)
    private String countryCode;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "location_id", nullable = false, unique = true)
    private Location location;

    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Characteristic> characteristics = new ArrayList<>();

    @Version
    private Long version;

    public void addCharacteristic(Characteristic c) {
        characteristics.add(c);
        c.setResource(this);
    }
    public void removeCharacteristic(Characteristic c) {
        characteristics.remove(c);
        c.setResource(null);
    }
}
