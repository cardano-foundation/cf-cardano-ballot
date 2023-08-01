package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.cardanofoundation.cip30.CIP30Verifier;

import java.util.Optional;
import java.util.function.Function;

@Getter
@Builder
@AllArgsConstructor
public class Vote {

    public static final Function<Vote, byte[]> VOTE_SERIALISER = createSerialiserFunction();

    private String coseSignature;

    @Builder.Default
    private Optional<String> cosePublicKey = Optional.empty();

    private static Function<Vote, byte[]> createSerialiserFunction() {
        return vote -> {
            var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());
            var verificationResult = cip30Verifier.verify();

            return Optional.ofNullable(verificationResult.getMessage()).orElse(new byte[0]);
        };
    }

}
