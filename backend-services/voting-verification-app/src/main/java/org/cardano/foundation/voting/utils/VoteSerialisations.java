package org.cardano.foundation.voting.utils;

import org.cardano.foundation.voting.domain.CoseWrappedVote;
import org.cardanofoundation.cip30.CIP30Verifier;

import java.util.Optional;
import java.util.function.Function;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash256;

public final class VoteSerialisations {

    public static final Function<CoseWrappedVote, byte[]> VOTE_SERIALISER = createSerialiserFunction();

    private static Function<CoseWrappedVote, byte[]> createSerialiserFunction() {
        return vote -> {
            var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());
            var verificationResult = cip30Verifier.verify();

            var bytes = Optional.ofNullable(verificationResult.getMessage()).orElse(new byte[0]);

            return blake2bHash256(bytes);
        };
    }

}
