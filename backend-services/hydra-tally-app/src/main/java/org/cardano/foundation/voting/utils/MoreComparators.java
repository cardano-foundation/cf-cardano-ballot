package org.cardano.foundation.voting.utils;

import org.cardano.foundation.voting.domain.UTxOCategoryResult;
import org.cardano.foundation.voting.domain.UTxOVote;
import org.cardano.foundation.voting.domain.Vote;

import java.util.Comparator;
import java.util.function.Function;

public class MoreComparators {

    public static Comparator<UTxOVote> createVoteTxHashAndTransactionIndexComparator() {
        return Comparator.comparing((Function<UTxOVote, String>) t -> t.utxo().getTxHash())
                .thenComparing(t -> t.utxo().getOutputIndex());
    }

    public static Comparator<UTxOCategoryResult> createCategoryResultTxHashAndTransactionIndexComparator() {
        return Comparator.comparing((Function<UTxOCategoryResult, String>) t -> t.utxo().getTxHash())
                .thenComparing(t -> t.utxo().getOutputIndex());
    }

    public static <T> Comparator<Vote> createVoteComparator() {
        return Comparator.comparing(Vote::voteId);
    }

}
