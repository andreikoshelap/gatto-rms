package com.gatto.consumer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "raw_json",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_rawjson_topic_part_offset",
                columnNames = {"topic", "partition", "offset"}
        )
)
@Getter @Setter
public class RawJson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String topic;

    // "partition" column name is fine on PostgreSQL
    @Column(name = "partition", nullable = false)
    private Integer partition;

    @Column(nullable = false)
    private Long offset;

    @Column(name = "message_key")
    private String messageKey;

    // Store original JSON payload as jsonb
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    // Optional headers map serialized to jsonb
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String headers;

    // Business time (if present in the message)
    @Column(name = "event_time")
    private OffsetDateTime eventTime;

    // Ingestion time
    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @PrePersist
    void prePersist() {
        if (receivedAt == null) {
            receivedAt = OffsetDateTime.now();
        }
    }
}
