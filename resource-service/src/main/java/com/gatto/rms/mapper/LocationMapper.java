package com.gatto.rms.mapper;

import com.gatto.rms.entity.Location;
import com.gatto.rms.view.LocationView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationView toView(Location entity);
    Location toEntity(LocationView view);
}
