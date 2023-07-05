package org.cardano.foundation.voting.service.address;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.cardano.foundation.voting.utils.MoreAddress.isMainnet;
import static org.cardano.foundation.voting.utils.MoreAddress.isTestnet;
import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class StakeAddressVerificationService {

    @Autowired
    private CardanoNetwork cardanoNetwork;

    public Either<Problem, Boolean> checkStakeAddress(String stakeAddress) {
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
