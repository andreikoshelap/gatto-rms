package com.gatto.rms.view;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ResourceView(
        Long id,
        String type,
        String countryCode,
        List<CharacteristicView> characteristics,
        LocationView location,
        Long version
) {
    public ResourceView {
        if (characteristics == null) {
            characteristics = List.of();
        }
    }
}
