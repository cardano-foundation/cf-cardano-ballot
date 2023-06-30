package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.exception.APIException;
import io.blockfrost.sdk.api.model.TransactionMetadataLabelJson;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;

import java.util.List;
import java.util.Map;

public class BlockfrostBlockchainDataMetadataService extends AbstractBlockfrostService implements BlockchainDataMetadataService {

    @Override
    @SneakyThrows
    public List<Map> fetchMetadataForLabel(String metadataLabel, int pageSize, int page) {
        try {
            var allTransactionMetadataJsonForLabel = metadataService.getTransactionMetadataJsonForLabel(metadataLabel, pageSize, page);

            return allTransactionMetadataJsonForLabel.stream()
                    .map(TransactionMetadataLabelJson::getJsonMetadata)
                    .map(json -> (java.util.Map) json)
                    .toList();

        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return List.of();
            }

            throw e;
        }
    }
}
