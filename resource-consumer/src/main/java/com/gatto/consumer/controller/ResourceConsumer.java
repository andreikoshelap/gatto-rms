package com.gatto.consumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.consumer.mapper.ResourceMapper;
import com.gatto.consumer.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class ResourceConsumer {
    private final ObjectMapper objectMapper;
    private final ResourceRepository repository;
    private final ResourceMapper mapper;

    @KafkaListener(topics = {"resource-created","resource-updated","resource-deleted"},
            concurrency = "6")
    @Transactional
    public void onMessage(@Payload String json,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          Acknowledgment ack) throws Exception {

        var view = objectMapper.readValue(json, com.gatto.rms.contracts.ResourceView.class);

        switch (topic) {
            case "resource-created", "resource-updated" -> {
                // idempotent upsert
                var entity = repository.findById(view.id())
                        .map(e -> { mapper.update(e, view); return e; })
                        .orElseGet(() -> mapper.toEntity(view));
                repository.save(entity);
            }
            case "resource-deleted" -> repository.deleteById(view.id());
            default -> throw new IllegalStateException("Unknown topic " + topic);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { ack.acknowledge(); }
        });
    }
}
