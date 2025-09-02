package com.gatto.rms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long id;
    private String streetAddress;
    private String city;
    private String postalCode;
    private String countryCode;
    private double latitude;
    private double longitude;
}

