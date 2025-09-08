package com.gatto.rms.contracts;


public record CharacteristicView(
        Long id,
        String code,
        CharacteristicType type,
        String value
) {}

