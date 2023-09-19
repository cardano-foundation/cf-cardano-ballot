package org.cardano.foundation.voting.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CompletableFutures {

    public static <T> CompletableFuture<List<T>> anyResultsOf(List<CompletableFuture<T>> completableFutures) {
        var allFutures = CompletableFuture
                .anyOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));

        return allFutures.thenApply(
                future -> {
                    return completableFutures.stream()
                            .map(CompletableFuture::join)
                            .toList();
                });
    }

}