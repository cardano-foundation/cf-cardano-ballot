package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.api.model.Utxo;

public record UTxOVote(Utxo utxo, VoteDatum voteDatum) {
}
