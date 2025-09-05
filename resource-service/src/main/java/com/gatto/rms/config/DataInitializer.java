package com.gatto.rms.config;

import com.gatto.rms.entity.*;
import com.gatto.rms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ResourceRepository repository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Initializing sample data...");

        Resource r1 = createResource("Viru väljak 4", "Tallinn", "10111", "EE",
                59.4370, 24.7536, ResourceType.METERING_POINT,
                new Location(1L, "Viru väljak 4", "Tallinn", "10111", "EE", 59.4370, 24.7536),
                new Characteristic(1L,"C1", CharacteristicType.CONSUMPTION_TYPE, "high"));

        Resource r2 = createResource("Harju 10", "Tartu", "51007", "EE",
                58.3776, 26.7290, ResourceType.CONNECTION_POINT,
                new Location(2L, "Harju 10", "Tartu", "51007", "EE", 58.3776, 26.7290),
                new Characteristic(2L,"C2", CharacteristicType.CONSUMPTION_TYPE, "medium"));

        Resource r3 = createResource("Brivibas iela 20", "Riga", "LV-1011", "LV",
                56.9496, 24.1052, ResourceType.METERING_POINT,
                new Location(3L, "Brivibas iela 20", "Riga", "LV-1011", "LV", 56.9496, 24.1052),
                new Characteristic(3L, "C3", CharacteristicType.CONSUMPTION_TYPE, "low"));

        Resource r4 = createResource("Pärnu mnt 22", "Tallinn", "10141", "EE",
                59.4315, 24.7468, ResourceType.METERING_POINT,
                new Location(4L, "Pärnu mnt 22", "Tallinn", "10141", "EE", 59.4315, 24.7468),
                new Characteristic(4L, "C4", CharacteristicType.CHARGING_POINT, "fast"));

        Resource r5 = createResource("Dzirnavu iela 67", "Riga", "LV-1011", "LV",
                56.9537, 24.1256, ResourceType.CONNECTION_POINT,
                new Location(5L, "Dzirnavu iela 67", "Riga", "LV-1011", "LV", 56.9537, 24.1256),
                new Characteristic(5L,"C5A", CharacteristicType.CONNECTION_POINT_STATUS, "active"),
                new Characteristic(6L,"C5B", CharacteristicType.CONNECTION_POINT_STATUS, "maintenance"));

        Resource r6 = createResource("Narva mnt 5", "Narva", "21003", "EE",
                59.3772, 28.1903, ResourceType.CONNECTION_POINT,
                new Location(6L, "Narva mnt 5", "Narva", "21003", "EE", 59.3772, 28.1903),
                new Characteristic(7L, "C6A", CharacteristicType.CONNECTION_POINT_STATUS, "inactive"),
                new Characteristic(8L,"C6B", CharacteristicType.CONNECTION_POINT_STATUS, "pending"));

        repository.saveAll(List.of(r1, r2, r3, r4, r5, r6));
        log.info("Sample data initialized.");
    }

    private Resource createResource(
            String address, String city, String postalCode, String country,
            double lat, double lon, ResourceType type, Location location, Characteristic... characteristics
    ) {
        Resource resource = new Resource();
        resource.setType(type);
        resource.setCountryCode(country);
        resource.setLocation(location);
        resource.setCharacteristics(List.of(characteristics));
        resource.setVersion(0L);

        return resource;
    }
}
