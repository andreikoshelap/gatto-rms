package com.gatto.consumer.view;

public record ResourceDiff(
        Long resourceId,
        Long versionFrom,
        Long versionTo,
        boolean typeChanged,
        boolean countryChanged,
        LocationChange locationChange,
        java.util.List<CharacteristicDelta> characteristics
) {}
