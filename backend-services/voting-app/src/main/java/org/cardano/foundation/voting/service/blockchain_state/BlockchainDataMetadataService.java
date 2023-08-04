package org.cardano.foundation.voting.service.blockchain_state;

import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;

import java.util.List;

public interface BlockchainDataMetadataService {

    default List<TransactionMetadataLabelCbor> fetchMetadataForLabel(String metadataLabel,
                                                                     int pageSize,
                                                                     int page,
                                                                     TransactionDetails.FinalityScore finalityScore) {
        return List.of();
    };

}


