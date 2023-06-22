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

    private ConfirmationScore confirmationScore;

    public enum ConfirmationScore {
        LOW,
        MEDIUM,
        HIGH;


        // TODO adjust this logic
        public static ConfirmationScore fromConfirmations(int transactionsConfirmations) {
            if (transactionsConfirmations < 10) {
                return LOW;
            }

            if (transactionsConfirmations < 100) {
                return MEDIUM;
            }

            return HIGH;
        }

    }

}
