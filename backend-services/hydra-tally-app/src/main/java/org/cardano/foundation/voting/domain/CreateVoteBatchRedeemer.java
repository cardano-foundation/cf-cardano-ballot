package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Constr(alternative = 0)
public class CreateVoteBatchRedeemer {

    public static CreateVoteBatchRedeemer create() {
        return new CreateVoteBatchRedeemer();
    }

}
