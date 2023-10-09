package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class TxLocalRequest {

    private String txId;

    public String key() {
        return String.format("%s|%s", getClass().getName(), txId);
    }

}
