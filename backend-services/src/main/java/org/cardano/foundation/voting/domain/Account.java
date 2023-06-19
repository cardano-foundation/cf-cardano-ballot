package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private String stakeAddress;
    private long votingPower;

}
