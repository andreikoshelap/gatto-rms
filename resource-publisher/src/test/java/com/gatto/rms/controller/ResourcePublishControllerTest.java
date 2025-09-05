package com.gatto.rms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.rms.service.KafkaPublisherService;
import com.gatto.rms.view.ResourceView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourcePublishController.class)
class ResourcePublishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // real ObjectMapper from Spring context

    @MockBean
    private KafkaPublisherService kafkaPublisherService;

    @Test
    @DisplayName("POST /publish/create -> publishes created event and returns 200")
    void publishCreate_ok() throws Exception {
        String body = "{\"id\":1}";

        mockMvc.perform(post("/publish/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // Capture the JSON sent to the publisher service
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaPublisherService).publishCreatedEvent(captor.capture());

        // Deserialize back to ResourceView and assert fields
        ResourceView sent = objectMapper.readValue(captor.getValue(), ResourceView.class);
        assertThat(sent.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("POST /publish/update -> publishes updated event and returns 200")
    void publishUpdate_ok() throws Exception {
        String body = "{\"id\":2}";

        mockMvc.perform(post("/publish/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaPublisherService).publishUpdatedEvent(captor.capture());

        ResourceView sent = objectMapper.readValue(captor.getValue(), ResourceView.class);
        assertThat(sent.id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("POST /publish/delete -> publishes deleted event and returns 200")
    void publishDelete_ok() throws Exception {
        String body = "{\"id\":3}";

        mockMvc.perform(post("/publish/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaPublisherService).publishDeletedEvent(captor.capture());

        ResourceView sent = objectMapper.readValue(captor.getValue(), ResourceView.class);
        assertThat(sent.id()).isEqualTo(3L);
    }
}
