package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.OnChainEventType;

import java.util.List;

@Getter
@Builder
public class CategoryRegistrationEnvelope {

    private OnChainEventType type;
    private String name;
    private String event;
    private String schemaVersion;

    private long creationSlot;

    @Builder.Default
    private boolean gdprProtection = true;

    @Builder.Default
    private boolean allowVoteChanging = false;

    @Builder.Default
    private boolean categoryResultsWhileVoting = false;

    private List<ProposalEnvelope> proposals;

}
