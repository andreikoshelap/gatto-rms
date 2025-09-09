package com.gatto.consumer.service;

import com.gatto.consumer.entity.GeoPoint;
import com.gatto.consumer.entity.ResourceEntity;
import com.gatto.consumer.repository.ResourceRepository;
import com.gatto.rms.contracts.LocationView;
import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceReadModelServiceImpl implements ResourceReadModelService {

    private final ResourceRepository repository;

    @Override
    public Optional<ResourceView> findViewById(Long id) {
        return repository.findById(id).map(this::toView);
    }

    // Entity -> record (only the fields available in the read model)
    private ResourceView toView(ResourceEntity e) {
        LocationView location = null;
        GeoPoint gp = e.getLocation();
        if (gp != null) {
            // Only what we have in the read model; address fields are unknown here
            location = new LocationView(
                    null,          // id (not stored in read model)
                    null,          // streetAddress
                    null,          // city
                    null,          // postalCode
                    e.getCountryCode(),
                    gp.getLatitude(),
                    gp.getLongitude()
            );
        }

        return new ResourceView(
                e.getId(),
                e.getType(),
                e.getCountryCode(),
                List.of(),
                location,
                e.getVersion()
        );
    }
}
