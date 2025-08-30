package com.gatto.rms.service;


import org.springframework.stereotype.Service;

@Service
public class ResourceService {
//    private final ResourceRepository repository;
//    private final ResourceMapper mappingService;

//    public ResourceService(ResourceRepository repository, ResourceMapper mappingService) {
//        this.repository = repository;
//        this.mappingService = mappingService;
//    }

//    public List<ResourceDTO> getAllResources() {
//        return repository.findAll().stream()
//                .map(mappingService::resourceToResourceDTO)
//                .collect(Collectors.toList());
//    }

//    public ResourceDTO save(ResourceDTO resourceDTO)  {
//        if (repository.existsById(resourceDTO.getId())) {
//            throw new ResourceAlreadyExistException();
//        }
//        Resource forSave = Resource.builder().build();
//        Optional<Resource> resourceById = Optional.empty();
//        if (resourceDTO.getId() != null) {
//            resourceById = repository.findById(resourceDTO.getId());
//        }
//        forSave.setId(resourceById.map(Resource::getId).orElse(null));
//
//        return mappingService.resourceToResourceDTO(repository.save(forSave));
//    }

//    public Optional<ResourceDTO> getById(Long id) {
//        return repository.findById(id).map(mappingService::resourceToResourceDTO);
//    }
//
//    public void deleteById(Long id) {
//        if (!repository.existsById(id)) {
//            throw new ResourceDoesNotExistException();
//        }
//        repository.deleteById(id);
//    }

}
