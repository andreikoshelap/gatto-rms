package com.gatto.rms.config;

import com.gatto.rms.entity.Characteristic;
import com.gatto.rms.entity.CharacteristicType;
import com.gatto.rms.entity.Location;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.entity.ResourceType;
import com.gatto.rms.repository.ResourceRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ResourceRepository repository;

    @PostConstruct
    @Transactional
    public void init() {
        // ===== Resource 1: Estonia =====
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
        link(r1, c1);

        repository.save(r1);

        // ===== Resource 2: Estonia =====
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
        link(r2, c2);

        repository.save(r2);

        // ===== Resource 3: Latvia =====
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
        link(r3, c3);

        repository.save(r3);

        // ===== Resource 4: Charging Point in Estonia =====
        Location loc4 = new Location();
        loc4.setStreetAddress("Pärnu mnt 22");
        loc4.setCity("Tallinn");
        loc4.setPostalCode("10141");
        loc4.setCountryCode("EE");
        loc4.setLatitude(59.4315);
        loc4.setLongitude(24.7468);

        Characteristic c4 = new Characteristic();
        c4.setCode("C4");
        c4.setType(CharacteristicType.CHARGING_POINT);
        c4.setValue("fast");

        Resource r4 = new Resource();
        r4.setType(ResourceType.METERING_POINT);
        r4.setCountryCode("EE");
        r4.setLocation(loc4);
        link(r4, c4);

        repository.save(r4);

        // ===== Resource 5: Latvia, 2 CONNECTION_POINT_STATUS characteristics =====
        Location loc5 = new Location();
        loc5.setStreetAddress("Dzirnavu iela 67");
        loc5.setCity("Riga");
        loc5.setPostalCode("LV-1011");
        loc5.setCountryCode("LV");
        loc5.setLatitude(56.9537);
        loc5.setLongitude(24.1256);

        Characteristic c5a = new Characteristic();
        c5a.setCode("C5A");
        c5a.setType(CharacteristicType.CONNECTION_POINT_STATUS);
        c5a.setValue("active");

        Characteristic c5b = new Characteristic();
        c5b.setCode("C5B");
        c5b.setType(CharacteristicType.CONNECTION_POINT_STATUS);
        c5b.setValue("maintenance");

        Resource r5 = new Resource();
        r5.setType(ResourceType.CONNECTION_POINT);
        r5.setCountryCode("LV");
        r5.setLocation(loc5);
        link(r5, c5a, c5b);

        repository.save(r5);

        // ===== Resource 6: Estonia, 2 CONNECTION_POINT_STATUS characteristics =====
        Location loc6 = new Location();
        loc6.setStreetAddress("Narva mnt 5");
        loc6.setCity("Narva");
        loc6.setPostalCode("21003");
        loc6.setCountryCode("EE");
        loc6.setLatitude(59.3772);
        loc6.setLongitude(28.1903);

        Characteristic c6a = new Characteristic();
        c6a.setCode("C6A");
        c6a.setType(CharacteristicType.CONNECTION_POINT_STATUS);
        c6a.setValue("inactive");

        Characteristic c6b = new Characteristic();
        c6b.setCode("C6B");
        c6b.setType(CharacteristicType.CONNECTION_POINT_STATUS);
        c6b.setValue("pending");

        Resource r6 = new Resource();
        r6.setType(ResourceType.CONNECTION_POINT);
        r6.setCountryCode("EE");
        r6.setLocation(loc6);
        link(r6, c6a, c6b);

        repository.save(r6);
    }

    /**
     * Связывает ресурс с характеристиками:
     *  - проставляет владельца связи на стороне Characteristic (c.setResource(r))
     *  - добавляет характеристики в коллекцию ресурса, инициализируя её при необходимости
     */
    private static void link(Resource r, Characteristic... cs) {
        if (r.getCharacteristics() == null) {
            r.setCharacteristics(new ArrayList<>());
        }
        for (Characteristic c : cs) {
            c.setResource(r);                 // владелец связи
            r.getCharacteristics().add(c);    // обратная сторона
        }
    }
}
