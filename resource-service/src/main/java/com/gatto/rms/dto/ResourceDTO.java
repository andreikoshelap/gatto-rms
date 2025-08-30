package com.gatto.rms.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResourceDTO {
    private Long id;
    private String type;
    private String countryCode;
    private List<CharacteristicDTO> characteristics;
}

