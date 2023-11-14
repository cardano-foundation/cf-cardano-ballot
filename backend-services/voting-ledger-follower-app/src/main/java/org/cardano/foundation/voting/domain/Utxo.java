package org.cardano.foundation.voting.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Utxo {

    @JsonProperty("address")
    private String address;

    @JsonProperty("tx_hash")
    private String txHash;

    @JsonProperty("tx_index")
    private int txIndex;

    @JsonProperty("inline_datum")
    private String inlineDatum;

}
