package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Constr(alternative = 1)
public class ReduceVoteBatchRedeemer {

    public static ReduceVoteBatchRedeemer create() {
            return new ReduceVoteBatchRedeemer();
    }

}
