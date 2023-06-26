package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.cip.cip30.CIP30DataSigner;
import com.bloxbean.cardano.client.cip.cip30.DataSignature;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
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
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.cardano.foundation.voting.service.transaction_submit.EventType.*;

@Service
@Slf4j
public class L1SubmissionService {

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

//  TODO how to make it more generic that we can submit transactions via this interface, this will be when Yaci is integrated
    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Autowired
    private BackendService backendService;

    @Autowired
    private MetadataSerialiser metadataSerialiser;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    public String submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        long absoluteSlot = blockchainDataChainTipService.getChainTip().getAbsoluteSlot();

        MetadataMap eventMetadataMap = metadataSerialiser.serialise(l1MerkleCommitments, absoluteSlot);
        Metadata metadata = serialiseMetadata(eventMetadataMap, COMMITMENTS);
        byte[] txData = serialiseTransaction(metadata);

        return transactionSubmissionService.submitTransaction(txData);
    }

    public String submitEvent(Event event) {
        long absoluteSlot = blockchainDataChainTipService.getChainTip().getAbsoluteSlot();

        MetadataMap eventMetadataMap = metadataSerialiser.serialise(event, absoluteSlot);
        Metadata metadata = serialiseMetadata(eventMetadataMap, EVENT_REGISTRATION);

        byte[] txData = serialiseTransaction(metadata);

        return transactionSubmissionService.submitTransaction(txData);
    }

    public String submitCategory(Event event, Category category) {
        long absoluteSlot = blockchainDataChainTipService.getChainTip().getAbsoluteSlot();

        MetadataMap eventMetadataMap = metadataSerialiser.serialise(event, category, absoluteSlot);
        Metadata metadata = serialiseMetadata(eventMetadataMap, CATEGORY_REGISTRATION);
        byte[] txData = serialiseTransaction(metadata);

        return transactionSubmissionService.submitTransaction(txData);
    }

    @SneakyThrows
    protected Metadata serialiseMetadata(MetadataMap childMetadata, EventType metadataType) {
        var stakeAddress = organiserAccount.stakeAddress();

        byte[] data = CborSerializationUtil.serialize(childMetadata.getMap());
        DataSignature dataSignature = CIP30DataSigner.INSTANCE.signData(stakeAddress.getBytes(), data, organiserAccount);

        var envelope = MetadataBuilder.createMap();
        envelope.put("type", metadataType.name());
        envelope.put("signature", dataSignature.signature());
        envelope.put("key", dataSignature.key());

        Metadata metadata = MetadataBuilder.createMetadata();
        metadata.put(metadataLabel, envelope);

        log.info("Metadata envelope:{}", envelope.toJson());

        log.info("Full metadata:{}", HexUtil.encodeHexString(metadata.serialize()));

        return metadata;
    }

    @SneakyThrows
    protected byte[] serialiseTransaction(Metadata metadata) {
        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);

        Tx tx = new Tx()
                .payToAddress(organiserAccount.baseAddress(), Amount.ada(2.0))
                .attachMetadata(metadata)
                .from(organiserAccount.baseAddress());

        return quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(organiserAccount))
                .buildAndSign()
                .serialize();
    }

}
