package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.util.Tuple;
import org.cardano.foundation.voting.domain.Vote;

import java.util.Comparator;
import java.util.function.Function;

public class MoreComparators {

    public static <T> Comparator<Tuple<Utxo, T>> createTxHashAndTransactionIndexComparator() {
        return Comparator.comparing((Function<Tuple<Utxo, T>, String>) t -> t._1.getTxHash())
                .thenComparing(t -> t._1.getOutputIndex());
    }

    public static <T> Comparator<Vote> createVoteComparator() {
        return Comparator.comparing(Vote::voteId);
    }

}
