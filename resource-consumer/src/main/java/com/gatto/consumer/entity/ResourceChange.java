package com.gatto.consumer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resource_change")
@lombok.Getter @lombok.Setter
public class ResourceChange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "version_from")
    private Long versionFrom;

    @Column(name = "version_to")
    private Long versionTo;

    @Column(nullable = false)
    private String reason; // CREATED/UPDATED/DELETED

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String diff;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "prev_json", columnDefinition = "jsonb")
    private String prevJson;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "curr_json", columnDefinition = "jsonb", nullable = false)
    private String currJson;

    @Column(name = "received_at", nullable = false)
    private java.time.OffsetDateTime receivedAt;

    @PrePersist
    void pre() {
        if (receivedAt == null) receivedAt = java.time.OffsetDateTime.now();
    }
}
