package com.gatto.consumer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_read_model")
public class ResourceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String type;

    @Column(length = 2, nullable = false)
    private String countryCode;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",  column = @Column(name = "lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "lng"))
    })
    private GeoPoint location;
    @Version
    private Long version;
}
