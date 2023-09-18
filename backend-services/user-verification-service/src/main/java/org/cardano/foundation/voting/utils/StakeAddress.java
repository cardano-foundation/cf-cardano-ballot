package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.common.model.Networks;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Slf4j
public final class StakeAddress {

    public static boolean isMainnet(String stakeAddress) {
        var addr = new Address(stakeAddress);

        return addr.getNetwork().equals(Networks.mainnet());
    }

    public static boolean isTestnet(String stakeAddress) {
        return !isMainnet(stakeAddress);
    }

    public static Either<Problem, Boolean> checkStakeAddress(CardanoNetwork network, String stakeAddress) {
        return checkIfAddressIsStakeAddress(network, stakeAddress)
                .flatMap(isStakeAddress -> {
                    if (!isStakeAddress) {
                        return Either.right(false);
                    }

                    return checkStakeAddressNetwork(network, stakeAddress);
                });
    }

    private static Either<Problem, Boolean> checkIfAddressIsStakeAddress(CardanoNetwork network, String address) {
        try {
            if (new Address(address).getDelegationCredential().isEmpty()) {
                return Either.left(Problem.builder()
                        .withTitle("NOT_STAKE_ADDRESS")
                        .withDetail("Address is not a stakeAddress, address:" + address)
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

    private static Either<Problem, Boolean> checkStakeAddressNetwork(CardanoNetwork network, String stakeAddress) {
        if (isMainnet(stakeAddress) && network.isMainnet()) {
            return Either.right(true);
        }

        if (isTestnet(stakeAddress) && network.isTestnet()) {
            return Either.right(true);
        }

        log.warn("Network mismatch, stakeAddress:{}, network:{}", stakeAddress, network);

        return Either.left(Problem.builder()
                .withTitle("STAKE_ADDRESS_NETWORK_MISMATCH")
                .withDetail("Stake address, stakeAddress:" + stakeAddress)
                .withStatus(BAD_REQUEST)
                .build()
        );
    }

}
