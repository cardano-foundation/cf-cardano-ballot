package org.cardano.foundation.voting.config;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.WalletType;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.AccountWalletSupplier;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.JsonUriWalletSupplierFactory;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.Wallet;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.WalletSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
@Slf4j
@AllArgsConstructor
public class CardanoConfig {

    private final Environment environment;

    private final ResourceLoader resourceLoader;

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
    public WalletSupplier walletSupplier(@Value("${l1.operator.wallet.type}") WalletType walletType,
                                         Network network,
                                         ObjectMapper objectMapper) throws IOException {
        return switch (walletType) {
            case MNEMONIC -> {
                val mnemonic = environment.getProperty("l1.operator.mnemonic");
                val mnemonicIndex = environment.getProperty("l1.operator.mnemonic.index", Integer.class, 0);
                val account = new Account(network, mnemonic, mnemonicIndex.intValue());

                yield new AccountWalletSupplier(account);
            }
            case CLI_JSON_FILE -> {
                val signingKeyFilePath = environment.getProperty("l1.operator.signing.key.file.path");
                val verificationKeyFilePath = environment.getProperty("l1.operator.verification.key.file.path");
                log.info("L1 Signing file path: {}", signingKeyFilePath);
                log.info("L1 Verification file path: {}", verificationKeyFilePath);

                assert signingKeyFilePath != null;
                assert verificationKeyFilePath != null;

                val jsonFileWalletSupplierFactory = new JsonUriWalletSupplierFactory(
                        resourceLoader.getResource(signingKeyFilePath).getURL(),
                        resourceLoader.getResource(verificationKeyFilePath).getURL(),
                        objectMapper
                );

                yield jsonFileWalletSupplierFactory.loadWallet();
            }
        };
    }

    @Bean
    public Wallet l1Wallet(Network network,
                           WalletSupplier walletSupplier) throws CborSerializationException {
        val wallet = walletSupplier.getWallet();
        val verificationKey = wallet.getVerificationKey();
        val secretKey = wallet.getSecretKey();

        log.info("L1 wallet address: {}", wallet.getAddress(network));
        log.info("L1 wallet verification key: {}", verificationKey.getCborHex());
        log.info("L1 wallet verification key type: {}", verificationKey.getType());
        log.info("L1 wallet verification key desc: {}", verificationKey.getDescription());

        log.info("L1 wallet secret key: {}", secretKey.getCborHex());
        log.info("L1 wallet secret key type: {}", secretKey.getType());
        log.info("L1 wallet secret key desc: {}", secretKey.getDescription());

        return wallet;
    }

}
