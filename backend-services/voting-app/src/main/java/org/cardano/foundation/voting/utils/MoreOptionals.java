package org.cardano.foundation.voting.utils;

import io.vavr.Tuple2;

import java.util.Optional;

public class MoreOptionals {

    public static <L, M, R> Optional<Tuple2<L, M>> allOf(Optional<L> o1,
                                                         Optional<M> o2) {
        return o1.flatMap(l -> o2.map(r -> new Tuple2<>(l, r)));
    }

}
