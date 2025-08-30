package com.gatto.producer.service;

import com.gatto.producer.dto.ResourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaSenderController {

    private final KafkaTemplate<String, ResourceDTO> kafkaTemplate;

    @PostMapping("/send-resource")
    public ResponseEntity<Void> send(@RequestBody ResourceDTO resource) {
        kafkaTemplate.send("resource.updated", resource);
        return ResponseEntity.ok().build();
    }
}
