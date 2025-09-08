package com.gatto.consumer.mapper;

import com.gatto.consumer.entity.ResourceEntity;
import com.gatto.rms.contracts.ResourceView;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    ResourceEntity toEntity(ResourceView view);
    void update(@MappingTarget ResourceEntity target, ResourceView view);
}
