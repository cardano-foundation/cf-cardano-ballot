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

    @Enc(value = "US_ASCII")
    private byte[] voteId;

    @Enc(value = "US_ASCII")
    private byte[] voterKey;

    @Enc(value = "US_ASCII")
    private byte[] categoryId;

    @Enc(value = "US_ASCII")
    private byte[] proposalId;

}
