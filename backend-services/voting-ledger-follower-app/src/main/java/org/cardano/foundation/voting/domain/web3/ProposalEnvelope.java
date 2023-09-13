package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProposalEnvelope {

    private String id;
    private String name;

}
