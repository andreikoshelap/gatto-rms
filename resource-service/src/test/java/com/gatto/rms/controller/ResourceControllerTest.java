package com.gatto.rms.controller;

import com.gatto.rms.service.ResourceService;
import com.gatto.rms.view.LocationView;
import com.gatto.rms.view.ResourceView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ResourceControllerTest {

    private ResourceService resourceService;
    private ResourceController controller;

    @BeforeEach
    void setUp() {
        resourceService = mock(ResourceService.class);
        controller = new ResourceController(resourceService);
    }

    @Test
    void testCreate() {
        ResourceView input =  ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(resourceService.save(1L, input)).thenReturn(input);

        ResponseEntity<ResourceView> response = controller.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(input, response.getBody());
    }

    @Test
    void testAll() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(resourceService.getAllResources()).thenReturn(List.of(view));

        List<ResourceView> result = controller.all();
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
    }

    @Test
    void testGetFound() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();
        when(resourceService.findById(1L)).thenReturn(Optional.of(view));

        ResponseEntity<ResourceView> response = controller.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(view, response.getBody());
    }

    @Test
    void testGetNotFound() {
        when(resourceService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ResourceView> response = controller.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOptimisticLockingFailure() {
        when(resourceService.findById(1L)).thenThrow(new ObjectOptimisticLockingFailureException("Resource", 1L));

        ResponseEntity<ResourceView> response = controller.get(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateOk() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(resourceService.save(1L, view)).thenReturn(view);

        ResponseEntity<ResourceView> response = controller.update(1L, view);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(view, response.getBody());
    }

    @Test
    void testUpdateNotFound() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(resourceService.save(1L, view)).thenThrow(NoSuchElementException.class);

        ResponseEntity<ResourceView> response = controller.update(1L, view);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateConflict() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(resourceService.save(1L, view)).thenThrow(ObjectOptimisticLockingFailureException.class);

        ResponseEntity<ResourceView> response = controller.update(1L, view);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testDeleteOk() {
        doNothing().when(resourceService).deleteById(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteNotFound() {
        doThrow(NoSuchElementException.class).when(resourceService).deleteById(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteConflict() {
        doThrow(ObjectOptimisticLockingFailureException.class).when(resourceService).deleteById(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
