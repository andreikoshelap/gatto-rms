package com.gatto.rms.controller;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.service.ResourceService;
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
        ResourceDTO input = new ResourceDTO();
        input.setId(1L);

        when(resourceService.save(1L, input)).thenReturn(input);

        ResponseEntity<ResourceDTO> response = controller.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(input, response.getBody());
    }

    @Test
    void testAll() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        when(resourceService.getAllResources()).thenReturn(List.of(dto));

        List<ResourceDTO> result = controller.all();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetFound() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        when(resourceService.findById(1L)).thenReturn(Optional.of(dto));

        ResponseEntity<ResourceDTO> response = controller.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetNotFound() {
        when(resourceService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ResourceDTO> response = controller.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOptimisticLockingFailure() {
        when(resourceService.findById(1L)).thenThrow(new ObjectOptimisticLockingFailureException("Resource", 1L));

        ResponseEntity<ResourceDTO> response = controller.get(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateOk() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        when(resourceService.save(1L, dto)).thenReturn(dto);

        ResponseEntity<ResourceDTO> response = controller.update(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testUpdateNotFound() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        when(resourceService.save(1L, dto)).thenThrow(NoSuchElementException.class);

        ResponseEntity<ResourceDTO> response = controller.update(1L, dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateConflict() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        when(resourceService.save(1L, dto)).thenThrow(ObjectOptimisticLockingFailureException.class);

        ResponseEntity<ResourceDTO> response = controller.update(1L, dto);

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
