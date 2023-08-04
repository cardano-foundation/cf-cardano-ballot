package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.exception.APIException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataMetadataService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
public class BlockfrostBlockchainDataMetadataService extends AbstractBlockfrostService implements BlockchainDataMetadataService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Override
    @SneakyThrows
    public List<TransactionMetadataLabelCbor> fetchMetadataForLabel(String metadataLabel,
                                                                    int pageSize,
                                                                    int page,
                                                                    TransactionDetails.FinalityScore finalityScore) {
        try {
            var allTransactionMetadataJsonForLabel = metadataService.getTransactionMetadataCborForLabel(metadataLabel, pageSize, page);

            return allTransactionMetadataJsonForLabel.stream()
                    .map(blockfrostType -> {
                        var maybeTransactionDetails = blockchainDataTransactionDetailsService.getTransactionDetails(blockfrostType.getTxHash());

                        if (maybeTransactionDetails.isEmpty()) {
                            log.warn("Transaction details not found for tx: {}, filtering out from processing", blockfrostType.getTxHash());

                            return Optional.<TransactionMetadataLabelCbor>empty();
                        }

                        var trxDetails = maybeTransactionDetails.orElseThrow();

                        if (trxDetails.getFinalityScore().getScore() < finalityScore.getScore()) {
                            log.warn("Transaction: {} is not VERY_HIGH yet. Skipping processing on chain event.", trxDetails.getTransactionHash());

                            return Optional.<TransactionMetadataLabelCbor>empty();
                        }

                        return Optional.of(TransactionMetadataLabelCbor
                                .builder()
                                .cborMetadata(blockfrostType.getCborMetadata())
                                .txHash(blockfrostType.getTxHash())
                                .slot(trxDetails.getAbsoluteSlot())
                                .build()
                        );
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (APIException e) {
            if (e.getErrorCode() == 404) {
                return List.of();
            }

            throw e;
        }
    }
}
