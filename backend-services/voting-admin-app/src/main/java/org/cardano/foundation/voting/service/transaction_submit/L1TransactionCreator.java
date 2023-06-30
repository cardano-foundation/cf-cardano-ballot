package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.cip.cip30.CIP30DataSigner;
import com.bloxbean.cardano.client.cip.cip30.DataSignature;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.metadata.Metadata;
import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CreateCategoryCommand;
import org.cardano.foundation.voting.domain.CreateEventCommand;
import org.cardano.foundation.voting.domain.OnChainEventType;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.cardano.foundation.voting.domain.OnChainEventType.CATEGORY_REGISTRATION;
import static org.cardano.foundation.voting.domain.OnChainEventType.EVENT_REGISTRATION;

@Service
@Slf4j
public class L1TransactionCreator {

    @Autowired
    private BackendService backendService;

    @Autowired
    private MetadataSerialiser metadataSerialiser;

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    public byte[] submitEvent(CreateEventCommand event) {
        var chainTip = blockchainDataChainTipService.getChainTip();

        MetadataMap eventMetadataMap = metadataSerialiser.serialise(event, chainTip.getAbsoluteSlot());
        Metadata metadata = serialiseMetadata(eventMetadataMap, EVENT_REGISTRATION);

        return serialiseTransaction(metadata);
    }

    public byte[] submitCategory(CreateCategoryCommand category) {
        var chainTip = blockchainDataChainTipService.getChainTip();

        MetadataMap eventMetadataMap = metadataSerialiser.serialise(category, chainTip.getAbsoluteSlot());
        Metadata metadata = serialiseMetadata(eventMetadataMap, CATEGORY_REGISTRATION);

        return serialiseTransaction(metadata);
    }

    @SneakyThrows
    protected Metadata serialiseMetadata(MetadataMap childMetadata, OnChainEventType metadataType) {
        var stakeAddress = organiserAccount.stakeAddress();
        var stakeAddressAccount = new Address(stakeAddress);

        byte[] data = childMetadata.toJson().getBytes(UTF_8);

        DataSignature dataSignature = CIP30DataSigner.INSTANCE.signData(stakeAddressAccount.getBytes(), data, organiserAccount);

        var envelope = MetadataBuilder.createMap();
        envelope.put("type", metadataType.name());
        envelope.put("signature", dataSignature.signature());
        envelope.put("key", dataSignature.key());
        envelope.put("format", "CIP-30");
        envelope.put("subFormat", "JSON"); // format in which actual CIP-30 data part is in

        Metadata metadata = MetadataBuilder.createMetadata();
        metadata.put(metadataLabel, envelope);

        log.info("Metadata envelope:{}", envelope.toJson());

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
