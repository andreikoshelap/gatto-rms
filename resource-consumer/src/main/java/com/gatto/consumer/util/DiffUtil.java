package com.gatto.consumer.util;

import com.gatto.consumer.view.CharOp;
import com.gatto.consumer.view.CharacteristicDelta;
import com.gatto.consumer.view.LocationChange;
import com.gatto.consumer.view.ResourceDiff;

public final class DiffUtil {

    // Compare nullable equality
    private static boolean eq(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }

    public static ResourceDiff compute(
            com.gatto.rms.contracts.ResourceView prev,
            com.gatto.rms.contracts.ResourceView curr
    ) {
        var typeChanged = !eq(prev == null ? null : prev.type(), curr.type());
        var countryChanged = !eq(prev == null ? null : prev.countryCode(), curr.countryCode());

        // location
        var prevLoc = prev == null ? null : prev.location();
        var currLoc = curr.location();
        var locChanged = !eq(prevLoc == null ? null : prevLoc.latitude(), currLoc.latitude())
                || !eq(prevLoc == null ? null : prevLoc.longitude(), currLoc.longitude());
        var locChange = new LocationChange(
                locChanged,
                prevLoc == null ? null : prevLoc.latitude(),
                prevLoc == null ? null : prevLoc.longitude(),
                currLoc.latitude(),
                currLoc.longitude()
        );

        // characteristics by key (id -> fallback to code)
        java.util.Map<String, com.gatto.rms.contracts.CharacteristicView> prevMap = new java.util.HashMap<>();
        if (prev != null && prev.characteristics() != null) {
            for (var c : prev.characteristics()) {
                prevMap.put(keyOf(c), c);
            }
        }
        java.util.Map<String, com.gatto.rms.contracts.CharacteristicView> currMap = new java.util.HashMap<>();
        if (curr.characteristics() != null) {
            for (var c : curr.characteristics()) {
                currMap.put(keyOf(c), c);
            }
        }

        var deltas = new java.util.ArrayList<CharacteristicDelta>();

        // UPDATED / ADDED
        for (var e : currMap.entrySet()) {
            var key = e.getKey();
            var now = e.getValue();
            var was = prevMap.get(key);
            if (was == null) {
                deltas.add(new CharacteristicDelta(
                        CharOp.ADDED, now.id(), now.code(),
                        null, now.type() == null ? null : now.type().name(),
                        null, now.value()
                ));
            } else {
                var typeChangedChar = !eq(was.type(), now.type());
                var valueChanged = !eq(was.value(), now.value());
                if (typeChangedChar || valueChanged) {
                    deltas.add(new CharacteristicDelta(
                            CharOp.UPDATED, now.id(), now.code(),
                            was.type() == null ? null : was.type().name(),
                            now.type() == null ? null : now.type().name(),
                            was.value(), now.value()
                    ));
                }
            }
        }
        // REMOVED
        for (var e : prevMap.entrySet()) {
            if (!currMap.containsKey(e.getKey())) {
                var was = e.getValue();
                deltas.add(new CharacteristicDelta(
                        CharOp.REMOVED, was.id(), was.code(),
                        was.type() == null ? null : was.type().name(), null,
                        was.value(), null
                ));
            }
        }

        return new ResourceDiff(
                curr.id(),
                prev == null ? null : prev.version(),
                curr.version(),
                typeChanged,
                countryChanged,
                locChange,
                deltas
        );
    }

    private static String keyOf(com.gatto.rms.contracts.CharacteristicView c) {
        return c.id() != null ? "ID:" + c.id() : "CODE:" + c.code();
    }
}
