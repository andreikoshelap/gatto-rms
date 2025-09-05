package com.gatto.rms.controller;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceDTO> create(@RequestBody ResourceDTO resourceDTO) {
        ResourceDTO saved = resourceService.save(resourceDTO.getId(), resourceDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ResourceDTO> all() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> get(@PathVariable("id") Long id) {
        try {
            ResourceDTO resource = resourceService.findById(id).orElseThrow();
            return ResponseEntity.ok(resource);
        } catch (NoSuchElementException e) {
            log.error("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while retrieving resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(@PathVariable("id") Long id, @RequestBody ResourceDTO updatedDTO) {
        try {
            log.info("Updating resource with ID: {}", id);
            ResourceDTO savedResource = resourceService.save(id, updatedDTO);
            log.info("Resource updated successfully: {}", savedResource);
            return ResponseEntity.ok(savedResource);
        } catch (NoSuchElementException e) {
            log.error("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            log.error("Resource ID or delete : {}", id);
            resourceService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.error("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while deleting resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
