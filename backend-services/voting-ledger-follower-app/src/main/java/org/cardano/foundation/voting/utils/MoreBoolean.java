package org.cardano.foundation.voting.utils;

import java.math.BigInteger;
import java.util.Optional;

public final class MoreBoolean {

    public static BigInteger toBigInteger(boolean value) {
        return value ? BigInteger.ONE : BigInteger.ZERO;
    }

    public static Optional<Boolean> fromBigInteger(BigInteger val) {
        return Optional.ofNullable(val).map(v -> v.equals(BigInteger.ONE));
    }

}
