package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import com.bloxbean.cardano.client.plutus.annotation.Enc;
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

    private String voteId;

    private byte[] voterKey;

    private String categoryId;

    private String proposalId;

}
