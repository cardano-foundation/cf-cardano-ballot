package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.CardanoOperator;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.CardanoOperatorSupplier;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.JacksonClasspathSecretKeyCardanoOperatorSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.cardanofoundation.hydra.core.utils.HexUtils.encodeHexString;

@Configuration
@Slf4j
public class CardanoConfig {

    @Bean
    public CardanoNetwork network(@Value("${cardano.network:main}") CardanoNetwork network) {
        log.info("Configured backend network:{}", network);

        return network;
    }

    @Bean
    public Network cardanoNetwork(CardanoNetwork cardanoNetwork) {
        return switch(cardanoNetwork) {
            case MAIN -> Networks.mainnet();
            case PREPROD -> Networks.preprod();
            case PREVIEW -> Networks.preview();
            case DEV -> Networks.testnet();
        };
    }

    @Bean
    public CardanoOperator l1CardanoOperator(CardanoOperatorSupplier cardanoOperatorSupplier) {
        var op =  cardanoOperatorSupplier.getOperator();

        log.info("L1 operator address: {}", op.getAddress());

        new Address(op.getAddress()).getPaymentCredentialHash().ifPresent(hash -> log.info("L1 operator verification key address (blake 224): {}", encodeHexString(hash)));

//        log.info("L1 operator verification key address (blake 224): {}", encodeHexString(blake2bHash224(HexUtils.decodeHexString(op.getVerificationKey().getCborHex()))));

        return op;
    }

    @Bean
    public CardanoOperatorSupplier l1SecretKeySupplier(@Value("${l1.operator.secret.file.path}") String secretFilePath,
                                                     Network network) throws CborSerializationException {
        log.info("L1 Secret file path: {}", secretFilePath);

        return new JacksonClasspathSecretKeyCardanoOperatorSupplier(secretFilePath, network);
    }

}
