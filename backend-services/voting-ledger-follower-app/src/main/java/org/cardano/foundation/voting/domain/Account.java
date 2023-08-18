package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class Account {

    private String stakeAddress;

    @Builder.Default
    // it will be empty when user is not staking
    private Optional<String> votingPower = Optional.empty();

    @Builder.Default
    // it will be empty when user is not staking
    private Optional<VotingPowerAsset> votingPowerAsset = Optional.empty();

}
