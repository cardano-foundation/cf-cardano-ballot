package org.cardano.foundation.voting.utils;

import java.util.Optional;

public final class Enums {

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {

        try {
            return Optional.of(Enum.valueOf(enumClass, value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
