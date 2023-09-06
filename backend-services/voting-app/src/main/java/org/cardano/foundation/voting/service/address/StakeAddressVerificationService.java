package org.cardano.foundation.voting.service.address;

import com.bloxbean.cardano.client.address.Address;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import static org.cardano.foundation.voting.utils.MoreAddress.isMainnet;
import static org.cardano.foundation.voting.utils.MoreAddress.isTestnet;
import static org.zalando.problem.Status.BAD_REQUEST;

@Component
@Slf4j
public class StakeAddressVerificationService {

    @Autowired
    private CardanoNetwork cardanoNetwork;

    public Either<Problem, Boolean> checkStakeAddress(String stakeAddress) {
        return checkIfAddressIsStakeAddress(stakeAddress)
                .flatMap(isStakeAddress -> {
                    if (!isStakeAddress) {
                        return Either.right(false);
                    }

                    return checkStakeAddressNetwork(stakeAddress);
                });
    }

    private Either<Problem, Boolean> checkIfAddressIsStakeAddress(String address) {
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

    private Either<Problem, Boolean> checkStakeAddressNetwork(String stakeAddress) {
        if (isMainnet(stakeAddress) && cardanoNetwork.isMainnet()) {
            return Either.right(true);
        }

        if (isTestnet(stakeAddress) && cardanoNetwork.isTestnet()) {
            return Either.right(true);
        }

        log.warn("Network mismatch, stakeAddress:{}, network:{}", stakeAddress, cardanoNetwork);

        return Either.left(Problem.builder()
                .withTitle("STAKE_ADDRESS_NETWORK_MISMATCH")
                .withDetail("Stake address, stakeAddress:" + stakeAddress)
                .withStatus(BAD_REQUEST)
                .build()
        );
    }

}
