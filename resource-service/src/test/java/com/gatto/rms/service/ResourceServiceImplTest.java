package com.gatto.rms.service;

import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.publisher.RestPublisherClient;
import com.gatto.rms.repository.ResourceRepository;
import com.gatto.rms.view.LocationView;
import com.gatto.rms.view.ResourceView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResourceServiceImplTest {

    @Mock
    private ResourceRepository repository;

    @Mock
    private ResourceMapper mappingService;

    @Mock
    private RestPublisherClient publisher;

    @InjectMocks
    private ResourceServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllResources_returnsMappedList() {
        Resource resource = new Resource();
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(repository.findAll()).thenReturn(List.of(resource));
        when(mappingService.toView(resource)).thenReturn(view);

        List<ResourceView> result = service.getAllResources();

        assertThat(result).containsExactly(view);
    }

    @Test
    void findById_returnsMappedDTO() {
        Resource resource = new Resource();
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(resource));
        when(mappingService.toView(resource)).thenReturn(view);

        Optional<ResourceView> result = service.findById(1L);

        assertThat(result).contains(view);
    }

    @Test
    void deleteById_whenExists_deletes() {
        Resource resource = new Resource();

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceView view = mappingService.toView(resource);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
        verify(publisher).publishDelete(view);
    }

    @Test
    void deleteById_whenNotExists_throws() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(1L))
                .isInstanceOf(ResourceDoesNotExistException.class);
    }

    @Test
    void save_whenIdIsNull_savesNewAndPublishes() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();
        Resource entity = new Resource();
        Resource saved = new Resource();
        ResourceView savedDto = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(mappingService.toEntity(view)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mappingService.toView(saved)).thenReturn(savedDto);

        ResourceView result = service.save(null, view);

        assertThat(result).isEqualTo(savedDto);
        verify(publisher).publishCreate(result);
    }

    @Test
    void save_whenIdExists_updatesResource() {
        ResourceView view = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        Resource existing = new Resource();
        existing.setCharacteristics(new ArrayList<>());

        Resource toSave = new Resource();
        toSave.setCharacteristics(new ArrayList<>());

        Resource updated = new Resource();
        ResourceView updatedDto = ResourceView.builder()
                .id(1L)
                .type("METERING_POINT")
                .countryCode("EE")
                .location(LocationView.builder().city("Tallinn").build())
                .build();

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(mappingService.toEntity(view)).thenReturn(toSave);
        when(repository.save(existing)).thenReturn(updated);
        when(mappingService.toView(updated)).thenReturn(updatedDto);

        ResourceView result = service.save(1L, view);

        assertThat(result).isEqualTo(updatedDto);
        verify(repository).save(existing);
        verify(publisher).publishUpdate(result);
    }
}
