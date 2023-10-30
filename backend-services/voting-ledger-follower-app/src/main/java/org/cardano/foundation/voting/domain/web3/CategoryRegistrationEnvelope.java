package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.domain.SchemaVersion;

import java.util.List;

@Getter
@Builder
@ToString
public class CategoryRegistrationEnvelope {

    private OnChainEventType type;
    private String id;
    private String event;
    private SchemaVersion schemaVersion;

    private long creationSlot;

    @Builder.Default
    private boolean gdprProtection = true; // TODO what should be the default?

    @Builder.Default
    private boolean allowVoteChanging = false;

    @Builder.Default
    private boolean categoryResultsWhileVoting = false;

    private List<ProposalEnvelope> proposals;

}
