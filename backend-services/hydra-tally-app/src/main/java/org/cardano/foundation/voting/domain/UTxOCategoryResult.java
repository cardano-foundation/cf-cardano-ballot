package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.api.model.Utxo;

public record UTxOCategoryResult(Utxo utxo, CategoryResultsDatum categoryResultsDatum) {
}
