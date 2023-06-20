package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionDetails {

    private String transactionHash;

    private long absoluteSlot;
    private String blockHash;

}
