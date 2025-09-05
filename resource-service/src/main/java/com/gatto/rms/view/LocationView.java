package com.gatto.rms.view;

import lombok.Builder;

@Builder
public record LocationView(
        Long id,
        String streetAddress,
        String city,
        String postalCode,
        String countryCode,
        double latitude,
        double longitude
) {
}
