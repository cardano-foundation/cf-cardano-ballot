package org.cardano.foundation.voting.service.address;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.util.HexUtil;
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

    public Either<Problem, Boolean> checkIfAddressIsStakeAddress(String address) {
        if (!address.startsWith("stake")) {
            return Either.left(Problem.builder()
                    .withTitle("NOT_STAKE_ADDRESS")
                    .withDetail("Address is not a stakeAddress, address:" + address)
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return Either.right(true);
    }

    public Either<Problem, Boolean> checkIfAddressIsStakeAddress(byte[] address) {
        var addr = new Address(address);

        if (!addr.isStakeKeyHashInDelegationPart()) {
            return Either.left(Problem.builder()
                    .withTitle("NOT_STAKE_ADDRESS")
                    .withDetail("Address is not a stakeAddress, address:" + HexUtil.encodeHexString(address))
                    .withStatus(BAD_REQUEST)
                    .build());
        }

        return Either.right(true);
    }

    public Either<Problem, Boolean> checkStakeAddressNetwork(String stakeAddress) {
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
