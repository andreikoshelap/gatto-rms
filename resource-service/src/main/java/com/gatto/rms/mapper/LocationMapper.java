package com.gatto.rms.mapper;

import com.gatto.rms.contracts.LocationView;
import com.gatto.rms.entity.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationView toView(Location entity);
    Location toEntity(LocationView view);
}
