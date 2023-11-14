package org.cardano.foundation.voting.utils;

public class MoreHash {

    public static int unsignedHash(Object o) {
        return o.hashCode() & 0xFFFFFFF;
    }

}
