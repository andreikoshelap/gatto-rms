package com.gatto.rms.service;

import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Resource;
import com.gatto.rms.error.ResourceDoesNotExistException;
import com.gatto.rms.mapper.ResourceMapper;
import com.gatto.rms.publisher.RestPublisherClient;
import com.gatto.rms.repository.ResourceRepository;
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
        ResourceDTO dto = new ResourceDTO();

        when(repository.findAll()).thenReturn(List.of(resource));
        when(mappingService.toDTO(resource)).thenReturn(dto);

        List<ResourceDTO> result = service.getAllResources();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void findById_returnsMappedDTO() {
        Resource resource = new Resource();
        ResourceDTO dto = new ResourceDTO();

        when(repository.findById(1L)).thenReturn(Optional.of(resource));
        when(mappingService.toDTO(resource)).thenReturn(dto);

        Optional<ResourceDTO> result = service.findById(1L);

        assertThat(result).contains(dto);
    }

    @Test
    void deleteById_whenExists_deletes() {
        Resource resource = new Resource();

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(resource));

        ResourceDTO dto = mappingService.toDTO(resource);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
        verify(publisher).publishDelete(dto);
    }

    @Test
    void deleteById_whenNotExists_throws() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(1L))
                .isInstanceOf(ResourceDoesNotExistException.class);
    }

    @Test
    void save_whenIdIsNull_savesNewAndPublishes() {
        ResourceDTO dto = new ResourceDTO();
        Resource entity = new Resource();
        Resource saved = new Resource();
        ResourceDTO savedDto = new ResourceDTO();

        when(mappingService.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mappingService.toDTO(saved)).thenReturn(savedDto);

        ResourceDTO result = service.save(null, dto);

        assertThat(result).isEqualTo(savedDto);
        verify(publisher).publishCreate(result);
    }

    @Test
    void save_whenIdExists_updatesResource() {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);

        Resource existing = new Resource();
        existing.setCharacteristics(new ArrayList<>());

        Resource toSave = new Resource();
        toSave.setCharacteristics(new ArrayList<>());

        Resource updated = new Resource();
        ResourceDTO updatedDto = new ResourceDTO();

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(mappingService.toEntity(dto)).thenReturn(toSave);
        when(repository.save(existing)).thenReturn(updated);
        when(mappingService.toDTO(updated)).thenReturn(updatedDto);

        ResourceDTO result = service.save(1L, dto);

        assertThat(result).isEqualTo(updatedDto);
        verify(repository).save(existing);
        verify(publisher).publishUpdate(result);
    }
}
