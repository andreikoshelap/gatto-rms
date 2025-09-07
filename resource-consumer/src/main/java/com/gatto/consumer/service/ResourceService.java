package com.gatto.consumer.service;

import com.gatto.consumer.mapper.ResourceMapper;
import com.gatto.consumer.repository.ResourceRepository;
import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository repo;
    private final ResourceMapper mapper;

    @Transactional
    public void upsert(ResourceView view) {
        // idempotent upsert by natural key (e.g., id); adjust to your schema
        var entity = mapper.toEntity(view);
        repo.save(entity); // ON CONFLICT-like via JPA merge semantics
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
