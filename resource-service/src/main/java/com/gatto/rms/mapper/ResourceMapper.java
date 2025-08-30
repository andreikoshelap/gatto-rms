package com.gatto.rms.mapper;

import com.gatto.rms.dto.CharacteristicDTO;
import com.gatto.rms.dto.LocationDTO;
import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Characteristic;
import com.gatto.rms.entity.Location;
import com.gatto.rms.entity.Resource;

import java.util.List;
import java.util.stream.Collectors;

public class ResourceMapper {
    public static ResourceDTO toDTO(Resource resource) {
        return ResourceDTO.builder()
                .id(resource.getId())
                .type(resource.getType() != null ? resource.getType().name() : null)
                .countryCode(resource.getCountryCode())
                .location(toLocationDTO(resource.getLocation()))
                .characteristics(toCharacteristicDTOList(resource.getCharacteristics()))
                .build();
    }

    public static LocationDTO toLocationDTO(Location location) {
        if (location == null) return null;
        return LocationDTO.builder()
                .id(location.getId())
                .streetAddress(location.getStreetAddress())
                .city(location.getCity())
                .postalCode(location.getPostalCode())
                .countryCode(location.getCountryCode())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    public static CharacteristicDTO toCharacteristicDTO(Characteristic c) {
        if (c == null) return null;
        return CharacteristicDTO.builder()
                .id(c.getId())
                .code(c.getCode())
                .type(c.getType() != null ? c.getType().name() : null)
                .value(c.getValue())
                .build();
    }

    public static List<CharacteristicDTO> toCharacteristicDTOList(List<Characteristic> list) {
        if (list == null) return null;
        return list.stream().map(ResourceMapper::toCharacteristicDTO).collect(Collectors.toList());
    }
}

