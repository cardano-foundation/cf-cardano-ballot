package org.cardano.foundation.voting.service.metadata;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.TransactionMetadataLabelCbor;
import org.cardano.foundation.voting.service.address.StakeAddressVerificationService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.json.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomMetadataService {

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @Autowired
    private CustomMetadataProcessor customMetadataProcessor;

    @Autowired
    private StakeAddressVerificationService stakeAddressVerificationService;

    @Value("${organiser.account.stakeAddress}")
    private String organiserStakeAccount;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Timed(value = "resource.metadata.passthrough", percentiles = { 0.3, 0.5, 0.95 })
    public void processRecentMetadataEventsPassThrough(List<TransactionMetadataLabelCbor> transactionMetadataLabelCbors) {
        log.info("processRecentMetadataEvents for metadata label {}", metadataLabel);

        customMetadataProcessor.processMetadataEvents(transactionMetadataLabelCbors);

        log.info("processRecentMetadataEvents for metadata label {} completed.", metadataLabel);
    }

}
