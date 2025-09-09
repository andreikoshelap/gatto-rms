package com.gatto.consumer.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RawJsonWriter {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    private static final String SQL = """
        INSERT INTO raw_json(topic, partition, offset, message_key, payload, headers, event_time, received_at)
        VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?)
        ON CONFLICT (topic, partition, offset) DO NOTHING
        """;

    public void write(ConsumerRecord<String, String> rec, OffsetDateTime eventTime) {
        Map<String, String> headersMap = new LinkedHashMap<>();
        for (Header h : rec.headers()) {
            headersMap.put(h.key(), h.value() == null ? null :
                    new String(h.value(), StandardCharsets.UTF_8));
        }
        try {
            String headersJson = headersMap.isEmpty() ? null : objectMapper.writeValueAsString(headersMap);
            jdbc.update(SQL,
                    rec.topic(),
                    rec.partition(),
                    rec.offset(),
                    rec.key(),
                    rec.value(),
                    headersJson,
                    eventTime,
                    OffsetDateTime.now()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to write raw_json", e);
        }
    }
}
