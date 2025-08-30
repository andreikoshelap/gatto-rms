package com.gatto.rms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Location {
    @Id
    @GeneratedValue
    private Long id;

    private String streetAddress;
    private String city;
    private String postalCode;
    private String countryCode;
}
