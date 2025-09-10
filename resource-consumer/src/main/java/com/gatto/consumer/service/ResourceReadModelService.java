package com.gatto.consumer.service;

import com.gatto.rms.contracts.ResourceView;

public interface ResourceReadModelService {

    void applyCreate(ResourceView curr);

    void applyUpdate(ResourceView curr);

    void applyDelete(Long id, Long version);
}
