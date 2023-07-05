package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.exception.APIException;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;

import java.util.List;

public class BlockfrostBlockchainDataMetadataService extends AbstractBlockfrostService implements BlockchainDataMetadataService {

    @Override
    @SneakyThrows
    public List<TransactionMetadataLabelCbor> fetchMetadataForLabel(String metadataLabel, int pageSize, int page) {
        try {
            var allTransactionMetadataJsonForLabel = metadataService.getTransactionMetadataCborForLabel(metadataLabel, pageSize, page);

            return allTransactionMetadataJsonForLabel.stream()
                    .map(blockfrostType -> org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor
                            .builder()
                            .cborMetadata(blockfrostType.getCborMetadata())
                            .txHash(blockfrostType.getTxHash())
                            .build())
                    .toList();

        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return List.of();
            }

            throw e;
        }
    }
}
