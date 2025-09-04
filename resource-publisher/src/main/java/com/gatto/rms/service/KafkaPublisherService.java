package com.gatto.rms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisherService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_CREATE = "resource-created-events";
    private static final String TOPIC_UPDATE = "resource-updated-events";
    private static final String TOPIC_DELETE = "resource-deleted-events";

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreatedEvent(String message) {
        logger.info("Publishing create message to Kafka: {}", message);
        kafkaTemplate.send(TOPIC_CREATE, message);
    }
    public void publishUpdatedEvent(String message) {
        logger.info("Publishing update message to Kafka: {}", message);
        kafkaTemplate.send(TOPIC_UPDATE, message);
    }
    public void publishDeletedEvent(String message) {
        logger.info("Publishing deleted message to Kafka: {}", message);
        kafkaTemplate.send(TOPIC_DELETE, message);
    }
//    private static final String CREATE_TOPIC = "resource-created-events";
//    private static final String UPDATE_TOPIC = "resource-updated-events";
//    private static final String DELETE_TOPIC = "resource-deleted-events";

//    public void publishCreate(ResourceDTO resourceDTO) {
//        kafkaTemplate.send(CREATE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
//    }
//
//    public void publishUpdate(ResourceDTO resourceDTO) {
//        kafkaTemplate.send(UPDATE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
//    }
//
//    public void publishDelete(ResourceDTO resourceDTO) {
//        kafkaTemplate.send(DELETE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
//    }
}
