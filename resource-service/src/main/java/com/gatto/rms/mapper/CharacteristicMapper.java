package com.gatto.rms.mapper;

import com.gatto.rms.entity.Characteristic;
import com.gatto.rms.view.CharacteristicView;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CharacteristicMapper {
    CharacteristicView toView(Characteristic entity);
    Characteristic toEntity(CharacteristicView view);
    List<CharacteristicView> toCharacteristicViewList(List<Characteristic> list);
}
