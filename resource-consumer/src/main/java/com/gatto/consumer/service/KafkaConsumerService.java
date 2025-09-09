package com.gatto.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.consumer.entity.ResourceChange;
import com.gatto.consumer.repository.ResourceChangeRepository;
import com.gatto.consumer.util.DiffUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final ResourceService resourceService;
    private final ResourceReadModelService readModelService; // to fetch previous snapshot
    private final ResourceChangeRepository changeRepo;

    @KafkaListener(
            topics = {"resource-created","resource-updated","resource-deleted"},
            groupId = "resource-consumer-group",
            concurrency = "2",
            containerFactory = "kafkaListenerContainerFactory" // AckMode.MANUAL
    )
    @Transactional
    public void consume(
            org.apache.kafka.clients.consumer.ConsumerRecord<String, String> rec,
            org.springframework.kafka.support.Acknowledgment ack
    ) throws Exception {

        var topic = rec.topic();
        var current = objectMapper.readValue(rec.value(), com.gatto.rms.contracts.ResourceView.class);
        var previous = readModelService.findViewById(current.id()).orElse(null);

        // decide reason
        var reason = switch (topic) {
            case "resource-created" -> "CREATED";
            case "resource-updated" -> "UPDATED";
            case "resource-deleted" -> "DELETED";
            default -> "UNKNOWN";
        };

        // compute and persist diff (skip for DELETED if не нужно)
        if (!"DELETED".equals(reason)) {
            var diffObj = DiffUtil.compute(prev, curr);
            var change = new ResourceChange();
            change.setResourceId(curr.id());
            change.setVersionFrom(diffObj.versionFrom());
            change.setVersionTo(diffObj.versionTo());
            change.setReason(reason);
            change.setDiff(objectMapper.writeValueAsString(diffObj));
            change.setPrevJson(prev == null ? null : objectMapper.writeValueAsString(prev));
            change.setCurrJson(objectMapper.writeValueAsString(curr));
            changeRepo.save(change);
        }

        // upsert / delete read model
        switch (reason) {
            case "CREATED", "UPDATED" -> resourceService.upsert(curr);
            case "DELETED" -> resourceService.deleteById(curr.id());
        }

        // ack AFTER successful commit
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override public void afterCommit() { ack.acknowledge(); }
                }
        );
    }
}
