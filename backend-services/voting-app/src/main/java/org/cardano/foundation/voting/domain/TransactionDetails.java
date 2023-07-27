package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionDetails {

    private String transactionHash;

    private long absoluteSlot;
    private String blockHash;

    private int transactionsConfirmations;

    private FinalityScore finalityScore;

    public enum FinalityScore {

        LOW(0),
        MEDIUM(1),
        HIGH(2),
        VERY_HIGH(3),
        FINAL(4); // TRANSACTION IS FINAL(!) - NO ROLLBACK POSSIBLE

        private final int score;

        FinalityScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public static FinalityScore fromConfirmations(int transactionsConfirmations) {
            if (transactionsConfirmations > 2160) { // 1,5 days
                return FinalityScore.FINAL;
            }

            if (transactionsConfirmations > 100) {
                return VERY_HIGH;
            }

            if (transactionsConfirmations >= 8) {
                return HIGH;
            }

            if (transactionsConfirmations >= 4) {
                return MEDIUM;
            }

            return LOW;
        }

    }

}
