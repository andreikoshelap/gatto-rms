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
        // Resource 1: Estonia
        Location loc1 = new Location();
        loc1.setStreetAddress("Viru väljak 4");
        loc1.setCity("Tallinn");
        loc1.setPostalCode("10111");
        loc1.setCountryCode("EE");
        loc1.setLatitude(59.4370);
        loc1.setLongitude(24.7536);

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

        // Resource 2: Estonia
        Location loc2 = new Location();
        loc2.setStreetAddress("Harju 10");
        loc2.setCity("Tartu");
        loc2.setPostalCode("51007");
        loc2.setCountryCode("EE");
        loc2.setLatitude(58.3776);
        loc2.setLongitude(26.7290);

        Characteristic c2 = new Characteristic();
        c2.setCode("C2");
        c2.setType(CharacteristicType.CONSUMPTION_TYPE);
        c2.setValue("medium");

        Resource r2 = new Resource();
        r2.setType(ResourceType.CONNECTION_POINT);
        r2.setCountryCode("EE");
        r2.setLocation(loc2);
        c2.setResource(r2);
        r2.setCharacteristics(List.of(c2));

        // Resource 3: Latvia
        Location loc3 = new Location();
        loc3.setStreetAddress("Brivibas iela 20");
        loc3.setCity("Riga");
        loc3.setPostalCode("LV-1011");
        loc3.setCountryCode("LV");
        loc3.setLatitude(56.9496);
        loc3.setLongitude(24.1052);

        Characteristic c3 = new Characteristic();
        c3.setCode("C3");
        c3.setType(CharacteristicType.CONSUMPTION_TYPE);
        c3.setValue("low");

        Resource r3 = new Resource();
        r3.setType(ResourceType.METERING_POINT);
        r3.setCountryCode("LV");
        r3.setLocation(loc3);
        c3.setResource(r3);
        r3.setCharacteristics(List.of(c3));

        repository.save(r1);
        repository.save(r2);
        repository.save(r3);
    }
}
