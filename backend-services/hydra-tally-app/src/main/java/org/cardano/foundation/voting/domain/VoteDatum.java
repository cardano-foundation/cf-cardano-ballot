package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Constr
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDatum {

    private String eventId;

    private String organiser;

    private String voteId;

    private byte[] voterKey;

    private String categoryId;

    private String proposalId;

    private Long voteScore;

}
