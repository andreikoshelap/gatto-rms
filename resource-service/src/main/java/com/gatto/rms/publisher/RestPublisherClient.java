package com.gatto.rms.publisher;

import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestPublisherClient {

    private final RestTemplate restTemplate;

    @Value("${publisher.base-url}")
    private String baseUrl; // e.g. http://resource-publisher.rms:8086/publish

    public void publishCreate(ResourceView view) {
        log.info("Calling resource-publisher to publish CREATE id={}", view.id());
        restTemplate.postForLocation(baseUrl + "/create", new HttpEntity<>(view));
    }

    public void publishUpdate(ResourceView view) {
        log.info("Calling resource-publisher to publish UPDATE id={}", view.id());
        restTemplate.postForLocation(baseUrl + "/update", new HttpEntity<>(view));
    }

    public void publishDelete(ResourceView view) {
        log.info("Calling resource-publisher to publish DELETE id={}", view.id());
        restTemplate.postForLocation(baseUrl + "/delete", new HttpEntity<>(view));
    }
}
