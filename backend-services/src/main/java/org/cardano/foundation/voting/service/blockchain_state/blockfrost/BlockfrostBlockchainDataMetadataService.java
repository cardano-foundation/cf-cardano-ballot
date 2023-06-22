package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.MetadataService;
import io.blockfrost.sdk.api.exception.APIException;
import io.blockfrost.sdk.api.model.TransactionMetadataLabelJson;
import io.blockfrost.sdk.api.util.Constants;
import io.blockfrost.sdk.impl.MetadataServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.blockfrost.sdk.api.util.OrderEnum.desc;

public class BlockfrostBlockchainDataMetadataService extends AbstractBlockfrostService implements BlockchainDataMetadataService {

    @Value("${l1.transaction.metadata.label}")
    private String metadataLabel;

    @Override
    @SneakyThrows
    public List<String> getAllMetadataForKey(String key) {
        try {
            List<TransactionMetadataLabelJson> allTransactionMetadataJsonForLabel = metadataService.getAllTransactionMetadataJsonForLabel(metadataLabel, desc);

            return allTransactionMetadataJsonForLabel.stream()
                    .map(TransactionMetadataLabelJson::getJsonMetadata)
                    .map(json -> (String)json)
                    .toList();
        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return List.of();
            }

            throw e;
        }
    }

}
