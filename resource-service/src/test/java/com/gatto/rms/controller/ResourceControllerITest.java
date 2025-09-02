package com.gatto.rms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.dto.CharacteristicDTO;
import com.gatto.rms.dto.LocationDTO;
import com.gatto.rms.dto.ResourceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
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

    @Test
    void shouldCreateThenRetrieveResource() throws Exception {
        // given
        LocationDTO loc1 = new LocationDTO();
        loc1.setStreetAddress("Viru väljak 4");
        loc1.setCity("Tallinn");
        loc1.setPostalCode("10111");
        loc1.setCountryCode("EE");
        loc1.setLatitude(59.4370);
        loc1.setLongitude(24.7536);
        ResourceDTO dto = new ResourceDTO();
        dto.setType("METERING_POINT");
        dto.setCountryCode("EE");
        dto.setLocation(loc1);
        dto.setCharacteristics(List.of(
                new CharacteristicDTO(null, "c33", "CHARGING_POINT", "fast"),
                new CharacteristicDTO(null, "c31", "CONSUMPTION_TYPE", "renewable")
        ));

        // when: create resource
        String responseBody = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();

        ResourceDTO created = objectMapper.readValue(responseBody, ResourceDTO.class);

        // then: fetch by ID
        mockMvc.perform(get("/api/resources/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.streetAddress").value("Viru väljak 4"))
                .andExpect(jsonPath("$.location.city").value("Tallinn"))
                .andExpect(jsonPath("$.location.postalCode").value("10111"))
                .andExpect(jsonPath("$.characteristics.length()").value(2));
    }

    @Test
    void shouldReturn404WhenResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/resources/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteResource() throws Exception {
        // Create
        ResourceDTO dto = new ResourceDTO();
        dto.setType("CONNECTION_POINT");
        dto.setCountryCode("EE");
        dto.setLocation(new LocationDTO());
        dto.setCharacteristics(Collections.emptyList());

        String createdJson = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readValue(createdJson, ResourceDTO.class).getId();

        // Delete
        mockMvc.perform(delete("/api/resources/" + id))
                .andExpect(status().isNoContent());

        // Ensure gone
        mockMvc.perform(get("/api/resources/" + id))
                .andExpect(status().isNotFound());
    }
}
