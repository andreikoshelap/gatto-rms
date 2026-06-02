package com.gatto.rms.controller;

import com.gatto.rms.contracts.LocationView;
import com.gatto.rms.contracts.ResourceView;
import com.gatto.rms.publisher.RestPublisherClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerITest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestPublisherClient publisherClient;

    @Test
    void shouldCreateThenRetrieveResource() throws Exception {
        // given
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder()
                        .streetAddress("Viru väljak 4")
                        .city("Tallinn")
                        .postalCode("10111")
                        .build())
                .build();

        // when: create resource
        String responseBody = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(view)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();

        ResourceView created = objectMapper.readValue(responseBody, ResourceView.class);

        // then: fetch by ID
        mockMvc.perform(get("/api/resources/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.streetAddress").value("Viru väljak 4"))
                .andExpect(jsonPath("$.location.city").value("Tallinn"))
                .andExpect(jsonPath("$.location.postalCode").value("10111"))
                .andExpect(jsonPath("$.characteristics.length()").value(0));
    }

    @Test
    void shouldReturn404WhenResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/resources/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteResource() throws Exception {
        // Create
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        String createdJson = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(view)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readValue(createdJson, ResourceView.class).id();

        // Delete
        mockMvc.perform(delete("/api/resources/" + id))
                .andExpect(status().isNoContent());

        // Ensure gone
        mockMvc.perform(get("/api/resources/" + id))
                .andExpect(status().isNotFound());
    }
}
