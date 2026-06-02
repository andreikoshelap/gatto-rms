package com.gatto.rms.controller;

import com.gatto.rms.contracts.ResourceView;
import com.gatto.rms.service.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/publish")
@RequiredArgsConstructor
public class ResourcePublishController {

    private final KafkaPublisherService kafkaPublisherService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<Void> publishCreate(@RequestBody ResourceView view) throws JacksonException {
        kafkaPublisherService.publishCreatedEvent(objectMapper.writeValueAsString(view), view.id());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> publishUpdate(@RequestBody ResourceView view) throws JacksonException {
        kafkaPublisherService.publishUpdatedEvent(objectMapper.writeValueAsString(view), view.id());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> publishDelete(@RequestBody ResourceView view) throws JacksonException {
        kafkaPublisherService.publishDeletedEvent(objectMapper.writeValueAsString(view), view.id());
        return ResponseEntity.ok().build();
    }
}
