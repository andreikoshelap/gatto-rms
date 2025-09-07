package com.gatto.rms.publisher;

import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestPublisherClient {

    private final RestTemplate restTemplate;

    private final String publisherUrl = "http://localhost:8086/publish";

    public void publishCreate(ResourceView view) {
        log.info("Calling resource-publisher to publish create");
        restTemplate.postForObject(publisherUrl + "/create", view, Void.class);
    }

    public void publishUpdate(ResourceView view) {
        log.info("Calling resource-publisher to publish update");
        restTemplate.postForObject(publisherUrl + "/update", view, Void.class);
    }

    public void publishDelete(ResourceView view) {
        log.info("Calling resource-publisher to publish delete");
        restTemplate.postForObject(publisherUrl + "/delete", view, Void.class);
    }
}
