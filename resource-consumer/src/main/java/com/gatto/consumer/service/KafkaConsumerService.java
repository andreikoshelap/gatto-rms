package com.gatto.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "resource-events", groupId = "resource-consumer-group")
    public void consume(String message) {
        logger.info("Received message from Kafka: {}", message);
        // Можно добавить сохранение в БД, агрегацию и т.д.
    }
}
