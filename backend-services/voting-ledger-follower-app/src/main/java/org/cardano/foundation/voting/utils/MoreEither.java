package org.cardano.foundation.voting.utils;

import io.vavr.control.Either;

import java.util.List;
import java.util.Optional;

public class MoreEither {

    public static <L, R> Optional<Either<L, R>> findFirstError(List<Either<L, R>> eitherList) {
        return eitherList
                .stream()
                .filter(Either::isLeft)
                .findFirst();
    }

}
