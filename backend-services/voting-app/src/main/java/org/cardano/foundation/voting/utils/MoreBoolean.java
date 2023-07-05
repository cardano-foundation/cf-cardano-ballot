package org.cardano.foundation.voting.utils;

import java.math.BigInteger;

public final class MoreBoolean {

    public static BigInteger toBigInteger(boolean value) {
        return value ? BigInteger.ONE : BigInteger.ZERO;
    }

    public static boolean fromBigInteger(BigInteger val) {
        return val.equals(BigInteger.ONE);
    }

    public static boolean fromInteger(Integer val) {
        return val.equals(1);
    }

}
