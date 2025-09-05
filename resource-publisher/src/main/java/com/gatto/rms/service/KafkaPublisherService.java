package com.gatto.rms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisherService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String CREATE_TOPIC = "resource-created-events";
    private static final String UPDATE_TOPIC = "resource-updated-events";
    private static final String DELETE_TOPIC = "resource-deleted-events";

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreatedEvent(String message) {
        String key = UUID.randomUUID().toString();
        logger.info("Publishing create message to Kafka with key {}: {}", key, message);
        kafkaTemplate.send(CREATE_TOPIC, key, message);
    }

    public void publishUpdatedEvent(String message) {
        String key = UUID.randomUUID().toString();
        logger.info("Publishing update message to Kafka with key {}: {}", key, message);
        kafkaTemplate.send(UPDATE_TOPIC, message);
    }

    public void publishDeletedEvent(String message) {
        String key = UUID.randomUUID().toString();
        logger.info("Publishing deleted message to Kafkawith key {}: {}", key, message);
        kafkaTemplate.send(DELETE_TOPIC, message);
    }
}
