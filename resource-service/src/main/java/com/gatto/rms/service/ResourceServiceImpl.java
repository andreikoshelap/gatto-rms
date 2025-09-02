package com.gatto.rms.service;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.publisher.ResourceKafkaPublisher;
import com.gatto.rms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository repository;
    private final ResourceMapper mappingService;
    private final ResourceKafkaPublisher publisher;

    @Override
    public List<ResourceDTO> getAllResources() {
        return repository.findAll().stream()
                .map(mappingService::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ResourceDTO> findById(Long id) {
        return repository.findById(id).map(mappingService::toDTO);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceDoesNotExistException();
        }
        Resource resource = repository.findById(id).orElseThrow();
        ResourceDTO dto = mappingService.toDTO(resource);
        repository.deleteById(id);
        publisher.publishDelete(dto); // Publish delete event after successful deletion
    }

    @Override
    public ResourceDTO save(Long id, ResourceDTO resourceDTO) {
        Resource forSave = mappingService.toEntity(resourceDTO);
        ResourceDTO resultDTO;
        if (resourceDTO.getId() != null && repository.existsById(id)) {
            Resource existingResource = repository.findById(id).orElseThrow();
            log.debug("Existing resource: {}", existingResource);
            existingResource.setType(forSave.getType());
            existingResource.setCountryCode(forSave.getCountryCode());
            existingResource.setLocation(forSave.getLocation());
            log.debug("Updated basic fields: type={}, countryCode={}, location={}", forSave.getType(),
                    forSave.getCountryCode(), forSave.getLocation());
            existingResource.getCharacteristics().clear();
            forSave.getCharacteristics().forEach(characteristic -> {
                characteristic.setResource(existingResource);
                existingResource.getCharacteristics().add(characteristic);
            });
            log.debug("Updated characteristics: {}", existingResource.getCharacteristics());
            Resource saved = repository.save(existingResource);
            resultDTO = mappingService.toDTO(saved);
            publisher.publishUpdate(resultDTO);
        } else {
            Resource saved = repository.save(forSave);
            resultDTO = mappingService.toDTO(saved);
            publisher.publishCreate(resultDTO);
        }
        return resultDTO;
    }
}
