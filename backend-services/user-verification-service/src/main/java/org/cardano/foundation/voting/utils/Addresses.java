package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.address.Address;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.WalletType;
import org.zalando.problem.Problem;

import static org.cardano.foundation.voting.utils.CardanoMoreAddress.isMainnet;
import static org.cardano.foundation.voting.utils.CardanoMoreAddress.isTestnet;
import static org.zalando.problem.Status.BAD_REQUEST;

@Slf4j
public final class Addresses {

    public static Either<Problem, Boolean> checkWalletId(ChainNetwork network, WalletType walletType, String walletId) {
        switch (walletType) {
            case WalletType.CARDANO:
                return checkIfAddressIsValid(walletId)
                        .flatMap(isStakeAddress -> {
                            if (!isStakeAddress) {
                                return Either.right(false);
                            }

                            return checkStakeAddressNetwork(network, walletId);
                        });
            case KERI:
                val aidValidationResultE = Keri.checkAid(walletId);
                if (aidValidationResultE.isLeft()) {
                    return Either.left(aidValidationResultE.getLeft());
                }

                return Either.right(true);
            default:
                return Either.left(Problem.builder()
                        .withTitle("UNSUPPORTED_WALLET_TYPE")
                        .withDetail("Unsupported wallet type:" + walletType)
                        .withStatus(BAD_REQUEST)
                        .build());
        }
    }

    private static Either<Problem, Boolean> checkIfAddressIsValid(String address) {
        try {
            if (new Address(address).getDelegationCredential().isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("NOT_STAKE_ADDRESS")
                        .withDetail("Address is not a walletId, address:" + address)
                        .withStatus(BAD_REQUEST)
                        .build());
            }

            return Either.right(true);
        } catch (Exception e) {
            log.warn("Unable to parse address, address:{}, reason:{}", address, e.getMessage());

            return Either.left(Problem.builder()
                    .withTitle("STAKE_ADDRESS_PARSE_ERROR")
                    .withDetail("Unable to parse address, address:" + address)
                    .withStatus(BAD_REQUEST)
                    .build());
        }
    }

    private static Either<Problem, Boolean> checkStakeAddressNetwork(ChainNetwork network, String walletId) {
        if (isMainnet(walletId) && network.isMainnet()) {
            return Either.right(true);
        }

        if (isTestnet(walletId) && network.isTestnet()) {
            return Either.right(true);
        }

        log.warn("Network mismatch, walletId:{}, network:{}", walletId, network);

        return Either.left(Problem.builder()
                .withTitle("STAKE_ADDRESS_NETWORK_MISMATCH")
                .withDetail("Stake address, walletId:" + walletId)
                .withStatus(BAD_REQUEST)
                .build()
        );
    }

}
