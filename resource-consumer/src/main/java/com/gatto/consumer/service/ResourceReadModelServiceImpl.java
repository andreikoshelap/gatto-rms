package com.gatto.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatto.consumer.entity.GeoPoint;
import com.gatto.consumer.entity.ResourceChange;
import com.gatto.consumer.entity.ResourceReadModel;
import com.gatto.consumer.mapper.ResourceMapper;
import com.gatto.consumer.repository.ResourceChangeRepository;
import com.gatto.consumer.repository.ResourceRepository;
import com.gatto.consumer.util.DiffUtil;
import com.gatto.rms.contracts.LocationView;
import com.gatto.rms.contracts.ResourceView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceReadModelServiceImpl implements ResourceReadModelService {

    private final ResourceRepository repository;
    private final ResourceMapper mapper;
    private final ObjectMapper objectMapper;           // for change log JSON
    private final ResourceChangeRepository changeRepo;

    // Entity -> record (only the fields available in the read model)
    private ResourceView toView(ResourceReadModel e) {
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

    @Transactional
    @Override
    public void applyCreate(ResourceView curr) {
        var prev = repository.findById(curr.id()).orElse(null);
        if (prev == null) {
            repository.save(mapper.toEntity(curr));
            logChange("CREATED", null, curr);
        } else {
            // treat as update if version is newer
            if (curr.version() != null && curr.version() > prev.getVersion()) {
                mapper.update(prev, curr);
                repository.save(prev);
                logChange("UPDATED", toView(prev), curr);
            }
            // else ignore stale/duplicate creates
        }
    }

    @Transactional
    @Override
    public void applyUpdate(ResourceView curr) {
        var prev = repository.findById(curr.id()).orElse(null);
        if (prev == null) {
            // upsert behavior: create if not exists
            repository.save(mapper.toEntity(curr));
            logChange("CREATED", null, curr);
            return;
        }
        if (curr.version() == null || curr.version() <= prev.getVersion()) {
            return; // ignore late/duplicate
        }
        var before = toView(prev);              // snapshot for diff
        mapper.update(prev, curr);
        repository.save(prev);
        logChange("UPDATED", before, curr);
    }

    @Transactional
    @Override
    public void applyDelete(Long id, Long version) {
        var prev = repository.findById(id).orElse(null);
        if (prev == null) return;               // already deleted or never created
        if (version != null && version <= prev.getVersion()) {
            return;                               // ignore stale delete
        }
        // log deletion with prev snapshot and no current
        logChange("DELETED", toView(prev), null);
        repository.deleteById(id);
    }

    private void logChange(String reason, ResourceView prev, ResourceView curr) {
        try {
            var diff = DiffUtil.compute(prev, curr); // your existing diff util
            var change = new ResourceChange();
            change.setResourceId( curr.id() );
            change.setVersionFrom(diff.versionFrom());
            change.setVersionTo(diff.versionTo());
            change.setReason(reason);
            change.setDiff(objectMapper.writeValueAsString(diff));
            change.setPrevJson(prev == null ? null : objectMapper.writeValueAsString(prev));
            change.setCurrJson(objectMapper.writeValueAsString(curr));
            changeRepo.save(change);
        } catch (Exception ex) {
            // make sure logging failures don't break consumption
             log.warn("Failed to log change", ex);
        }
    }
}
