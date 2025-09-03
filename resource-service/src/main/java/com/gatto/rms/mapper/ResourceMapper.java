package com.gatto.rms.mapper;

import com.gatto.rms.dto.CharacteristicDTO;
import com.gatto.rms.dto.LocationDTO;
import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Characteristic;
import com.gatto.rms.entity.Location;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.entity.ResourceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResourceMapper {

    public Resource toEntity(ResourceDTO updatedDTO) {
        if (updatedDTO == null) {
            return null;
        }
        Resource entity = new Resource();
        entity.setId(updatedDTO.getId());
        if (updatedDTO.getType() != null) {
            entity.setType(ResourceType.valueOf(updatedDTO.getType()));
        }
        entity.setCountryCode(updatedDTO.getCountryCode());
        if (updatedDTO.getLocation() != null) {
            Location location = getLocation(updatedDTO);
            entity.setLocation(location);
        }
        if (updatedDTO.getCharacteristics() != null) {
            List<Characteristic> characteristics = updatedDTO.getCharacteristics().stream().map(dto -> {
                Characteristic c = new Characteristic();
                c.setId(null);
                c.setCode(dto.getCode());
                if (dto.getType() != null) {
                    c.setType(com.gatto.rms.entity.CharacteristicType.valueOf(dto.getType()));
                }
                c.setValue(dto.getValue());
                return c;
            }).collect(Collectors.toList());
            entity.setCharacteristics(characteristics);
        }
        return entity;
    }

    private static Location getLocation(ResourceDTO updatedDTO) {
        Location location = new Location();
        location.setId(updatedDTO.getLocation().getId());
        location.setStreetAddress(updatedDTO.getLocation().getStreetAddress());
        location.setCity(updatedDTO.getLocation().getCity());
        location.setPostalCode(updatedDTO.getLocation().getPostalCode());
        location.setCountryCode(updatedDTO.getLocation().getCountryCode());
        location.setLatitude(updatedDTO.getLocation().getLatitude());
        location.setLongitude(updatedDTO.getLocation().getLongitude());
        return location;
    }

    public ResourceDTO toDTO(Resource resource) {
        return ResourceDTO.builder()
                .id(resource.getId())
                .type(resource.getType() != null ? resource.getType().name() : null)
                .countryCode(resource.getCountryCode())
                .location(toLocationDTO(resource.getLocation()))
                .characteristics(toCharacteristicDTOList(resource.getCharacteristics()))
                .build();
    }

    public LocationDTO toLocationDTO(Location location) {
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

    public CharacteristicDTO toCharacteristicDTO(Characteristic c) {
        if (c == null) return null;
        return CharacteristicDTO.builder()
                .id(c.getId())
                .code(c.getCode())
                .type(c.getType() != null ? c.getType().name() : null)
                .value(c.getValue())
                .build();
    }

    public List<CharacteristicDTO> toCharacteristicDTOList(List<Characteristic> list) {
        if (list == null) return null;
        return list.stream().map(this::toCharacteristicDTO).collect(Collectors.toList());
    }
}
