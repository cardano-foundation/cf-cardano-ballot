package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
public class VoteEnvelope {

    private String id;
    private String address;
    private String event;
    private String category;
    private String proposal;
    @Builder.Default
    private Optional<String> proposalText = Optional.empty(); // proposal text is only available for GDPR sensitive events, e.g. where proposal is simply an ID
    private String network;
    private String votedAt;

    @Builder.Default
    private Optional<String> votingPower = Optional.empty(); // voting power is only available for STAKE_BASED or BALANCE_BASED events

    public long getVotedAtSlot() {
        return Long.parseLong(votedAt);
    }

}
