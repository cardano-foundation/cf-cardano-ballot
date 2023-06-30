package org.cardano.foundation.voting.utils;

import com.bloxbean.cardano.client.exception.AddressExcepion;
import io.vavr.control.Either;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import static com.bloxbean.cardano.client.address.util.AddressUtil.bytesToAddress;

public final class Bech32 {

    public static Either<Problem, String> decode(byte[] address) {
        try {
            var stakeAddress = bytesToAddress(address);

            return Either.right(stakeAddress);
        } catch (AddressExcepion e) {
            return Either.left(
                    Problem.builder()
                    .withTitle("INVALID_ADDRESS")
                    .withDetail("Invalid bech32 address")
                    .withStatus(Status.BAD_REQUEST)
                    .withDetail(e.getMessage())
                    .build()
            );
        }
    }

}
