package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private String stakeAddress;

    private AccountStatus accountStatus;

    private int epochNo;

    private String votingPower;

    private VotingPowerAsset votingPowerAsset;

    private CardanoNetwork network;

    public enum AccountStatus {
        ELIGIBLE,
        NOT_ELIGIBLE
    }

}
