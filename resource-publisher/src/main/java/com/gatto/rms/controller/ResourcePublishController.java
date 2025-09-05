package com.gatto.rms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.service.KafkaPublisherService;
import com.gatto.rms.view.ResourceView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publish")
@RequiredArgsConstructor
public class ResourcePublishController {

    private final KafkaPublisherService kafkaPublisherService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<Void> publishCreate(@RequestBody ResourceView view) throws JsonProcessingException {
        kafkaPublisherService.publishCreatedEvent(objectMapper.writeValueAsString(view));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> publishUpdate(@RequestBody ResourceView view) throws JsonProcessingException {
        kafkaPublisherService.publishUpdatedEvent(objectMapper.writeValueAsString(view));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> publishDelete(@RequestBody ResourceView view) throws JsonProcessingException {
        kafkaPublisherService.publishDeletedEvent(objectMapper.writeValueAsString(view));
        return ResponseEntity.ok().build();
    }
}
