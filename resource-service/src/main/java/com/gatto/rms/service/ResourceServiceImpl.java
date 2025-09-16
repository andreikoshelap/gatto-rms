package com.gatto.rms.service;

import com.gatto.rms.contracts.ResourceView;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository repository;
    private final ResourceMapper mappingService;
    private final AfterCommitPublisher afterCommitPublisher; // publish only after DB commit

    @Override
    public List<ResourceView> getAllResources() {
        return repository.findAll().stream()
                .map(mappingService::toView)
                .toList();
    }

    @Override
    public Optional<ResourceView> findById(Long id) {
        return repository.findById(id).map(mappingService::toView);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Resource resource = repository.findById(id)
                .orElseThrow(ResourceDoesNotExistException::new);

        ResourceView view = mappingService.toView(resource);

        repository.delete(resource);

        afterCommitPublisher.publishDelete(view);
        log.debug("Deleted resource id={}", id);
    }

    @Override
    @Transactional
    public ResourceView create(ResourceView resourceView) {
        Resource entity = mappingService.toEntity(resourceView);

        Resource saved = repository.save(entity);
        ResourceView view = mappingService.toView(saved);

        afterCommitPublisher.publishCreate(view);
        log.debug("Created resource id={} type={} country={}", saved.getId(), saved.getType(), saved.getCountryCode());
        return view;
    }

    @Override
    @Transactional
    public ResourceView update(Long id, ResourceView resourceView) {
        if (resourceView.id() == null || !resourceView.id().equals(id)) {
            throw new ResourceDoesNotExistException();
        }

        Resource existing = repository.findById(id)
                .orElseThrow(ResourceDoesNotExistException::new);

        Resource incoming = mappingService.toEntity(resourceView);

        existing.setType(incoming.getType());
        existing.setCountryCode(incoming.getCountryCode());
        existing.setLocation(incoming.getLocation());

        existing.getCharacteristics().clear();
        if (incoming.getCharacteristics() != null) {
            incoming.getCharacteristics().forEach(ch -> {
                existing.getCharacteristics().add(ch);
            });
        }

        Resource saved = repository.save(existing);
        ResourceView view = mappingService.toView(saved);

        afterCommitPublisher.publishUpdate(view);
        log.debug("Updated resource id={} type={} country={}", saved.getId(), saved.getType(), saved.getCountryCode());
        return view;
    }
}
