package org.cardano.foundation.voting.service.transaction_submit;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.cip.cip30.CIP30DataSigner;
import com.bloxbean.cardano.client.cip.cip30.DataSignature;
import com.bloxbean.cardano.client.common.cbor.CborSerializationUtil;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.metadata.Metadata;
import com.bloxbean.cardano.client.metadata.MetadataBuilder;
import com.bloxbean.cardano.client.metadata.MetadataMap;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.L1MerkleCommitment;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.cardano.foundation.voting.domain.metadata.OnChainEventType.COMMITMENTS;

@Service
@Slf4j
public class L1TransactionCreator {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Autowired
    private BackendService backendService;

    @Autowired
    private MetadataSerialiser metadataSerialiser;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("organiser_account")
    private Account organiserAccount;

    @Value("${l1.transaction.metadata.label:12345}")
    private int metadataLabel;

    public byte[] submitMerkleCommitments(List<L1MerkleCommitment> l1MerkleCommitments) {
        var chainTip = blockchainDataChainTipService.getChainTip();
        MetadataMap eventMetadataMap = metadataSerialiser.serialise(l1MerkleCommitments, chainTip.getAbsoluteSlot());
        Metadata metadata = serialiseMetadata(eventMetadataMap, COMMITMENTS);

        return serialiseTransaction(metadata);
    }

    @SneakyThrows
    protected Metadata serialiseMetadata(MetadataMap childMetadata, OnChainEventType onChainEventType) {
        var stakeAddress = organiserAccount.stakeAddress();
        var stakeAddressAccount = new Address(stakeAddress);

        var data = CborSerializationUtil.serialize(childMetadata.getMap());
        var hashedData = Blake2bUtil.blake2bHash224(data);

        DataSignature dataSignature = CIP30DataSigner.INSTANCE.signData(
                stakeAddressAccount.getBytes(), hashedData,
                organiserAccount.stakeHdKeyPair().getPrivateKey().getKeyData(),
                organiserAccount.stakeHdKeyPair().getPublicKey().getKeyData()
        );
        var envelope = MetadataBuilder.createMap();
        envelope.put("type", onChainEventType.name());
        envelope.put("signature", dataSignature.signature()); // CIP-30
        envelope.put("key", dataSignature.key()); // CIP-30

        envelope.put("payload", childMetadata);
        envelope.put("signatureType", "HASH_ONLY"); // CIP-30 extension

        envelope.put("format", "CIP-30");
        envelope.put("subFormat", "CBOR");

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
