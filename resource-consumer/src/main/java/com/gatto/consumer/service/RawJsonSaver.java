package com.gatto.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.consumer.entity.RawJson;
import com.gatto.consumer.repository.RawJsonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RawJsonSaver {

    private final RawJsonRepository repo;
    private final ObjectMapper objectMapper;

    public void save(ConsumerRecord<String, String> rec, OffsetDateTime eventTime) {
        // Build headers map as String -> String
        Map<String, String> headersMap = new LinkedHashMap<>();
        for (Header h : rec.headers()) {
            headersMap.put(h.key(), h.value() == null ? null : new String(h.value(), StandardCharsets.UTF_8));
        }

        String headersJson = null;
        try {
            headersJson = headersMap.isEmpty() ? null : objectMapper.writeValueAsString(headersMap);
        } catch (Exception e) {
            // swallow header serialization errors to not block ingestion
        }

        RawJson r = new RawJson();
        r.setTopic(rec.topic());
        r.setPartition(rec.partition());
        r.setOffset(rec.offset());
        r.setMessageKey(rec.key());
        r.setPayload(rec.value());   // String JSON; @JdbcTypeCode(JSON) will store as jsonb
        r.setHeaders(headersJson);
        r.setEventTime(eventTime);
        // receivedAt is set in @PrePersist

        // Optional dedupe by unique key
        if (!repo.existsByTopicAndPartitionAndOffset(r.getTopic(), r.getPartition(), r.getOffset())) {
            repo.save(r);
        }
    }
}
