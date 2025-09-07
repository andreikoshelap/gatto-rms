package com.gatto.rms.controller;


import com.gatto.rms.contracts.ResourceView;
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
    public ResponseEntity<ResourceView> create(@RequestBody ResourceView resourceView) {
        ResourceView saved = resourceService.save(resourceView.id(), resourceView);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<ResourceView> all() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceView> get(@PathVariable Long id) {
        try {
            ResourceView resource = resourceService.findById(id).orElseThrow();
            return ResponseEntity.ok(resource);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while retrieving resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceView> update(@PathVariable Long id, @RequestBody ResourceView view) {
        try {
            if (view.id() != null && !view.id().equals(id)) {
                log.warn("Path ID {} != payload ID {}", id, view.id());
            }
            ResourceView savedResource = resourceService.save(id, view);
            return ResponseEntity.ok(savedResource);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            log.info("Deleting resource with ID: {}", id);
            resourceService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while deleting resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
