package com.gatto.rms.service;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.publisher.ResourceKafkaPublisher;
import com.gatto.rms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository repository;
    private final ResourceMapper mappingService;
    private final ResourceKafkaPublisher publisher;

//    public ResourceService(ResourceRepository repository, ResourceMapper mappingService,  ResourceKafkaPublisher publisher) {
//        this.repository = repository;
//        this.mappingService = mappingService;
//        this.publisher = publisher;
//    }

    public List<ResourceDTO> getAllResources() {
        return repository.findAll().stream()
                .map(mappingService::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ResourceDTO> findById(Long id) {
        return repository.findById(id).map(mappingService::toDTO);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceDoesNotExistException();
        }
        repository.deleteById(id);
    }

    public ResourceDTO save(Long id, ResourceDTO resourceDTO) {
        Resource forSave = mappingService.toEntity(resourceDTO);
        if (resourceDTO.getId() != null && repository.existsById(id)) {
            Resource existingResource = repository.findById(id).orElseThrow();
            log.debug("Existing resource: {}", existingResource);

            // Update basic fields
            existingResource.setType(forSave.getType());
            existingResource.setCountryCode(forSave.getCountryCode());
            existingResource.setLocation(forSave.getLocation());
            log.debug("Updated basic fields: type={}, countryCode={}, location={}", forSave.getType(),
                    forSave.getCountryCode(), forSave.getLocation());

            // Update characteristics
            existingResource.getCharacteristics().clear();
            forSave.getCharacteristics().forEach(characteristic -> {
                characteristic.setResource(existingResource);
                existingResource.getCharacteristics().add(characteristic);
            });
            log.debug("Updated characteristics: {}", existingResource.getCharacteristics());
            Resource savedResource = repository.save(existingResource);
            log.info("Resource updated successfully: {}", savedResource);
            return mappingService.toDTO(savedResource);

        }
        Resource saved = repository.save(forSave);
        publisher.publish(saved);
        return mappingService.toDTO(saved);
    }
}
