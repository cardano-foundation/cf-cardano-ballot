package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class CategoryResult {

    private String id;

    private List<ProposalResult> proposalResults;

}
