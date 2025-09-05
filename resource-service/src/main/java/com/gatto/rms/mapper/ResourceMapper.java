package com.gatto.rms.mapper;

import com.gatto.rms.entity.Characteristic;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.entity.ResourceType;
import com.gatto.rms.view.ResourceView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResourceMapper {
    private final LocationMapper locationMapper;
    private final CharacteristicMapper characteristicMapper;
    /* ====================== toEntity ====================== */

    public Resource toEntity(ResourceView view) {
        if (view == null) return null;

        Resource entity = new Resource();
        entity.setId(view.id());
        if (view.type() != null) {
            entity.setType(ResourceType.valueOf(view.type()));
        }
        entity.setCountryCode(view.countryCode());

        if (view.location() != null) {
            entity.setLocation(locationMapper.toEntity(view.location()));
        }

        if (view.characteristics() != null) {
            List<Characteristic> characteristics = view.characteristics().stream()
                    .map(characteristicMapper::toEntity)
                    .collect(Collectors.toList());

            entity.setCharacteristics(characteristics);
        }

        if (hasVersion(view)) {
            entity.setVersion(view.version());
        }

        return entity;
    }

    /* ====================== toView ====================== */

    public ResourceView toView(Resource e) {
        if (e == null) return null;
        return new ResourceView(
                e.getId(),
                e.getType() != null ? e.getType().name() : null,
                e.getCountryCode(),
                characteristicMapper.toCharacteristicViewList(e.getCharacteristics()),
                locationMapper.toView(e.getLocation()),
                e.getVersion()
        );
    }

    private boolean hasVersion(ResourceView view) {
        try {
            view.version();
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }
}
