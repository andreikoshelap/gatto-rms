package com.gatto.consumer.entity;

import jakarta.persistence.*;

@Entity
@lombok.Getter @lombok.Setter
@Table(name = "resource_read_model")
public class ResourceReadModel {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;                // set from event payload.id()

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    // Store source version from the event; do NOT annotate with @Version
    // unless you deliberately want JPA optimistic locking here.
    @Column(name = "version", nullable = false)
    private Long version;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat",  column = @Column(name = "lat")),
            @AttributeOverride(name = "lng", column = @Column(name = "lng"))
    })
    private GeoPoint location;

}
