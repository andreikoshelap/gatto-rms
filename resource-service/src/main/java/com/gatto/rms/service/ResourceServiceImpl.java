package com.gatto.rms.service;

import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.publisher.RestPublisherClient;
import com.gatto.rms.repository.ResourceRepository;
import com.gatto.rms.view.ResourceView;
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
    private final RestPublisherClient restPublisherClient;


    @Override
    public List<ResourceView> getAllResources() {
        return repository.findAll().stream()
                .map(mappingService::toView)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ResourceView> findById(Long id) {
        return repository.findById(id).map(mappingService::toView);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceDoesNotExistException();
        }
        Resource resource = repository.findById(id).orElseThrow();
        log.debug("Will delete resource: {}", resource);
        repository.deleteById(id);
        ResourceView dto = mappingService.toView(resource);
        restPublisherClient.publishDelete(dto);
    }

    @Override
    public ResourceView save(Long id, ResourceView resourceView) {
        Resource forSave = mappingService.toEntity(resourceView);
        ResourceView view;
        if (resourceView.id() != null && repository.existsById(id)) {
            Resource existingResource = repository.findById(id).orElseThrow();
            log.debug("Existing resource: {}", existingResource);
            existingResource.setType(forSave.getType());
            existingResource.setCountryCode(forSave.getCountryCode());
            existingResource.setLocation(forSave.getLocation());
            log.debug("Updated basic fields: type={}, countryCode={}, location={}", forSave.getType(),
                    forSave.getCountryCode(), forSave.getLocation());
            existingResource.getCharacteristics().clear();
            forSave.getCharacteristics().forEach(characteristic ->
                    existingResource.getCharacteristics().add(characteristic));
            log.debug("Updated characteristics: {}", existingResource.getCharacteristics());
            Resource saved = repository.save(existingResource);
            view = mappingService.toView(saved);
            restPublisherClient.publishUpdate(view);
        } else {
            log.debug("Creating new resource with type={}, countryCode={}, location={}",
                    forSave.getType(), forSave.getCountryCode(), forSave.getLocation());

            if (forSave.getCharacteristics() != null && !forSave.getCharacteristics().isEmpty()) {
                forSave.getCharacteristics().forEach(c ->
                        log.debug("New characteristic: code={}, type={}, value={}",
                                c.getCode(), c.getType(), c.getValue())
                );
            } else {
                log.debug("No characteristics provided for new resource");
            }

            Resource saved = repository.save(forSave);
            log.debug("Resource saved with ID={}, total characteristics={}",
                    saved.getId(), saved.getCharacteristics() != null ? saved.getCharacteristics().size() : 0);

            view = mappingService.toView(saved);
            restPublisherClient.publishCreate(view);
        }
        return view;
    }
}
