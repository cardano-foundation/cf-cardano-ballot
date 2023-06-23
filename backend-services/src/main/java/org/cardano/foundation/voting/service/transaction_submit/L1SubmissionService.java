package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.metadata.Metadata;
import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.entity.Category;
import org.cardano.foundation.voting.domain.entity.Event;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

//  TODO how to make it more generic that we can submit transactions via this interface, this will be when Yaci is integrated
    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @Autowired
    private BackendService backendService;

    @Autowired
    private MetadataSerialiser metadataSerialiser;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    @Value("${cardano-client-lib.timeout.in.minutes:1}")
    private int transactionTimeoutInMinutes;

    public String submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        return sendMetadataTransaction(metadataSerialiser.serialise(l1MerkleCommitments));
    }

    public String submitEvent(Event event) {
        return sendMetadataTransaction(metadataSerialiser.serialise(event));
    }

    public String submitCategory(Event event, Category category) {
        return sendMetadataTransaction(metadataSerialiser.serialise(event, category));
    }

    @SneakyThrows
    private String sendMetadataTransaction(MetadataMap metadataMap) {
        Metadata metadata = MetadataBuilder.createMetadata();
        metadata.put(metadataLabel, metadataMap);

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);

        Tx tx = new Tx()
                .payToAddress(organiserAccount.baseAddress(), Amount.ada(2.0))
                .attachMetadata(metadata)
                .from(organiserAccount.baseAddress());

        var serialisedTx = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(organiserAccount))
                .buildAndSign()
                .serialize();

        log.info("Submitting transaction: {}", HexUtil.encodeHexString(serialisedTx));

        log.info("Tx Metadata: {}", metadataMap.toJson());

        return transactionSubmissionService.submitTransaction(serialisedTx);
    }

}
