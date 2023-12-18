package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ProposalResult {

    private String id;

    private String name;

    private String voteCount;

    private String votingPower;

}
