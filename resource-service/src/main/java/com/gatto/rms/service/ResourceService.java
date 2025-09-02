package com.gatto.rms.service;

import com.gatto.rms.dto.ResourceDTO;
import java.util.List;
import java.util.Optional;

public interface ResourceService {
    List<ResourceDTO> getAllResources();
    Optional<ResourceDTO> findById(Long id);
    void deleteById(Long id);
    ResourceDTO save(Long id, ResourceDTO resourceDTO);
}
