package com.gatto.rms.service;


import com.gatto.rms.contracts.ResourceView;

import java.util.List;
import java.util.Optional;

public interface ResourceService {
    List<ResourceView> getAllResources();
    Optional<ResourceView> findById(Long id);
    void deleteById(Long id);
    ResourceView create(ResourceView resource);
    ResourceView update(Long id, ResourceView resource);
}
