package com.gatto.rms.mapper;


import com.gatto.rms.dto.ResourceDTO;
import com.gatto.rms.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    ResourceDTO resourceToResourceDTO(Resource order);

    Resource resourceDTOToResource(ResourceDTO orderDTO);
}
