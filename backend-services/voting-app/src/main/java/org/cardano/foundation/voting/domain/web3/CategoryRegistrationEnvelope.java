package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;

import java.util.List;

@Getter
@Builder
public class CategoryRegistrationEnvelope {

    private OnChainEventType type;
    private String name;
    private String event;
    private String schemaVersion;
    private String gdprProtection;

    private List<ProposalEnvelope> proposals;

    private long creationSlot;

    public boolean isGdprProtection() {
        return Boolean.parseBoolean(gdprProtection);
    }

}
