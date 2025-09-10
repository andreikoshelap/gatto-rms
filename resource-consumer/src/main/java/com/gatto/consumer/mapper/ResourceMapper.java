package com.gatto.consumer.mapper;

import com.gatto.consumer.entity.ResourceReadModel;
import com.gatto.rms.contracts.ResourceView;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    ResourceReadModel toEntity(ResourceView view);
    void update(@MappingTarget ResourceReadModel target, ResourceView view);
}
