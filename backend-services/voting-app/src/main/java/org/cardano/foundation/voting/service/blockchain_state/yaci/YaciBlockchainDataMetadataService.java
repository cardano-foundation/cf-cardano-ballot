package org.cardano.foundation.voting.service.blockchain_state.yaci;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;

import java.util.List;

@Slf4j
public class YaciBlockchainDataMetadataService implements BlockchainDataMetadataService {

    @Override
    public List<TransactionMetadataLabelCbor> fetchMetadataForLabel(String metadataLabel, int pageSize, int page, TransactionDetails.FinalityScore finalityScore) {
        // TODO when Yaci is ready
        return null;
    }

}
