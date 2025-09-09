package com.gatto.consumer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "raw_json",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_rawjson_topic_part_offset",
                columnNames = {"topic", "kafka_partition", "kafka_offset"}
        )
)
@Getter @Setter
public class RawJson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String topic;

    @Column(name = "kafka_partition", nullable = false)
    private Integer partition;

    @Column(name = "kafka_offset", nullable = false)
    private Long offset;

    @Column(name = "message_key")
    private String messageKey;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String headers;

    @Column(name = "event_time")
    private java.time.OffsetDateTime eventTime;

    @Column(name = "received_at", nullable = false)
    private java.time.OffsetDateTime receivedAt;

    @PrePersist void prePersist() {
        if (receivedAt == null) receivedAt = java.time.OffsetDateTime.now();
    }
}
