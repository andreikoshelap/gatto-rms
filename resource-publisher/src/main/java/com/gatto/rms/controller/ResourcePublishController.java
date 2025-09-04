package com.gatto.rms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.service.KafkaPublisherService;
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
    public ResponseEntity<Void> publishCreate(@RequestBody ResourceDTO dto) throws JsonProcessingException {
        kafkaPublisherService.publishCreatedEvent(objectMapper.writeValueAsString(dto));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> publishUpdate(@RequestBody ResourceDTO dto) throws JsonProcessingException {
        kafkaPublisherService.publishUpdatedEvent(objectMapper.writeValueAsString(dto));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> publishDelete(@RequestBody ResourceDTO dto) throws JsonProcessingException {
        kafkaPublisherService.publishDeletedEvent(objectMapper.writeValueAsString(dto));
        return ResponseEntity.ok().build();
    }
}
