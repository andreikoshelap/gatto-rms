package com.gatto.rms.view;

import com.gatto.rms.entity.CharacteristicType;

public record CharacteristicView(
        Long id,
        String code,
        CharacteristicType type,
        String value
) {}

