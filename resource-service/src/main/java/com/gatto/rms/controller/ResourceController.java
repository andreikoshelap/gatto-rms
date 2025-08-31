package com.gatto.rms.controller;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {
    private final ResourceRepository repository;


    @PostMapping
    public Resource create(@RequestBody Resource resource) {
        return repository.save(resource);
    }


    @GetMapping
    public List<ResourceDTO> all() {
        return repository.findAll().stream()
                .map(ResourceMapper::toDTO)
                .toList();
    }


    @GetMapping("/{id}")
    public Resource get(@PathVariable("id") Long id) {
        try {
            return repository.findById(id).orElseThrow();
        } catch ( ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while retrieving resource with ID: {}", id, e);
            throw new RuntimeException("The resource was updated or deleted by another transaction. Please try again.");
        }
    }


    @PutMapping("/{id}")
    public Resource update(@PathVariable("id") Long id, @RequestBody Resource updated) {
        try {
            log.info("Updating resource with ID: {}", id);
            Resource existingResource = repository.findById(id).orElseThrow();
            log.debug("Existing resource: {}", existingResource);

            // Update basic fields
            existingResource.setType(updated.getType());
            existingResource.setCountryCode(updated.getCountryCode());
            existingResource.setLocation(updated.getLocation());
            log.debug("Updated basic fields: type={}, countryCode={}, location={}", updated.getType(), updated.getCountryCode(), updated.getLocation());

            // Update characteristics
            existingResource.getCharacteristics().clear();
            updated.getCharacteristics().forEach(characteristic -> {
                characteristic.setResource(existingResource);
                existingResource.getCharacteristics().add(characteristic);
            });
            log.debug("Updated characteristics: {}", existingResource.getCharacteristics());

            Resource savedResource = repository.save(existingResource);
            log.info("Resource updated successfully: {}", savedResource);
            return savedResource;
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating resource with ID: {}", id, e);
            throw new RuntimeException("The resource was updated or deleted by another transaction. Please try again.");
        }
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        try {
            Resource resource = repository.findById(id).orElseThrow();
            repository.delete(resource);
        } catch (ObjectOptimisticLockingFailureException  e) {
            log.error("Optimistic locking failure while deleting resource with ID: {}", id, e);
            throw new RuntimeException("The resource was updated or deleted by another transaction. Please try again.");
        }
    }
}
