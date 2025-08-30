package com.gatto.rms.config;

import com.gatto.rms.entity.*;
import com.gatto.rms.repository.ResourceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final ResourceRepository repository;

    @PostConstruct
    public void init() {
        Location loc1 = new Location();
        loc1.setStreetAddress("Main St 1");
        loc1.setCity("Tallinn");
        loc1.setPostalCode("10001");
        loc1.setCountryCode("EE");


        Characteristic c1 = new Characteristic();
        c1.setCode("C1");
        c1.setType(CharacteristicType.CONSUMPTION_TYPE);
        c1.setValue("high");


        Resource r1 = new Resource();
        r1.setType(ResourceType.METERING_POINT);
        r1.setCountryCode("EE");
        r1.setLocation(loc1);
        c1.setResource(r1);
        r1.setCharacteristics(List.of(c1));


        repository.save(r1);


    }
}
