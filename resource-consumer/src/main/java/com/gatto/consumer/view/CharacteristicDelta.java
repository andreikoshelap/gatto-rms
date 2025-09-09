package com.gatto.consumer.view;

public record CharacteristicDelta(
        CharOp op,
        Long id,
        String code,
        String oldType, String newType,
        String oldValue, String newValue
) {}
