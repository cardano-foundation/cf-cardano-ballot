package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Data;
import org.cardano.foundation.voting.domain.entity.Vote;

@Data
@Builder
public class VoteReceipt {

    private Vote vote;

}
