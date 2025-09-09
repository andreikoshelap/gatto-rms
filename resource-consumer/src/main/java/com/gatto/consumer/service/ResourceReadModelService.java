package com.gatto.consumer.service;

import com.gatto.rms.contracts.ResourceView;

import java.util.Optional;

public interface ResourceReadModelService {
    Optional<ResourceView> findViewById(Long id);
}
