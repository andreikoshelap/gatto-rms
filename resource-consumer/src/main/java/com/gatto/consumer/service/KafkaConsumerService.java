package com.gatto.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final ResourceReadModelService readModelService; // to fetch previous snapshot
    private final RawJsonSaver rawJsonSaver;

    @KafkaListener(
            topics = {"resource-created-events", "resource-updated-events", "resource-deleted-events"},
            groupId = "resource-consumer-group",
            concurrency = "2",
            containerFactory = "kafkaListenerContainerFactory" // AckMode.MANUAL
    )
    @Transactional
    public void consume(ConsumerRecord<String, String> rec, Acknowledgment ack
    ) throws Exception {

        var topic = rec.topic();
        rawJsonSaver.save(rec, null);
        var view = toView(rec);
        switch (topic) {
            case "resource-created-events" -> readModelService.applyCreate(view);
            case "resource-updated-events" -> readModelService.applyUpdate(view);
            case "resource-deleted-events" -> readModelService.applyDelete(view.id(), view.version());
            default -> { /* ignore */ }
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { ack.acknowledge(); }
        });
    }

    public ResourceView toView(ConsumerRecord<String, String> rec) {
        try {
            return objectMapper.readValue(rec.value(), ResourceView.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse ResourceView from record", e);
        }
    }
}
