package com.gatto.rms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.entity.ResourceType;
import com.gatto.rms.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = new Resource();
        resource.setId(1L);
        resource.setType(ResourceType.METERING_POINT);
        resource.setCountryCode("EE");
        resource.setLocation(null);
        resource.setCharacteristics(Collections.emptyList());

        repository.save(resource);
    }

//    @Test
//    void testGetResourceById() throws Exception {
//        mockMvc.perform(get("/api/resources/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.type").value("METERING_POINT"));
//    }

    @Test
    void testCreateResource() throws Exception {
        Resource newResource = new Resource();
        newResource.setType(ResourceType.CONNECTION_POINT);
        newResource.setCountryCode("LV");
        newResource.setLocation(null);
        newResource.setCharacteristics(Collections.emptyList());

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CONNECTION_POINT"))
                .andExpect(jsonPath("$.countryCode").value("LV"));
    }

    @Test
    void testUpdateResource() throws Exception {
        resource.setCountryCode("FI");

        mockMvc.perform(put("/api/resources/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode").value("FI"));
    }

    @Test
    void testDeleteResource() throws Exception {
        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isOk());

        Optional<Resource> deletedResource = repository.findById(1L);
        assert (deletedResource.isEmpty());
    }
}
