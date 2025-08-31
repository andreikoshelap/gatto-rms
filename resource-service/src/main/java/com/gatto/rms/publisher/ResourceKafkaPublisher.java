package com.gatto.rms.publisher;

import com.gatto.rms.entity.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "resource-events";

    public void publish(Resource resource) {
        kafkaTemplate.send(TOPIC, resource.getId().toString(), resource);
    }
}
