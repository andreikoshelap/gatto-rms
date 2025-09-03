package com.gatto.rms.dto;

import com.gatto.rms.entity.ResourceType;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private Long id;
    private String type;
    private String countryCode;
    private List<CharacteristicDTO> characteristics;
    private LocationDTO location;
    private Long version;
}

