package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProposalEnvelope {

    private String id;
    private String name;

}
