package com.gatto.producer.service;

import com.gatto.producer.dto.ResourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ResourceDTO> kafkaTemplate;

    public void sendResource(ResourceDTO dto) {
        kafkaTemplate.send("resource.updated", dto);
    }
}
