package com.gatto.producer.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResourceDTO {
    private Long id;
    private String type;
    private String countryCode;
    private Long locationId;
//    private List<Characteristic> characteristics;
}
