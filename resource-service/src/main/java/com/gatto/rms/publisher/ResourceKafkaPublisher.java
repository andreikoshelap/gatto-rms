package com.gatto.rms.publisher;

import com.gatto.rms.dto.ResourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CREATE_TOPIC = "resource-created-events";
    private static final String UPDATE_TOPIC = "resource-updated-events";
    private static final String DELETE_TOPIC = "resource-deleted-events";

    public void publishCreate(ResourceDTO resourceDTO) {
        kafkaTemplate.send(CREATE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
    }

    public void publishUpdate(ResourceDTO resourceDTO) {
        kafkaTemplate.send(UPDATE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
    }

    public void publishDelete(ResourceDTO resourceDTO) {
        kafkaTemplate.send(DELETE_TOPIC, resourceDTO.getId().toString(), resourceDTO);
    }
}
