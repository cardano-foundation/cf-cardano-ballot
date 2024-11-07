package org.cardano.foundation.voting.utils;

import lombok.val;
import org.cardano.foundation.voting.domain.WrappedVote;
import org.cardanofoundation.cip30.CIP30Verifier;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash256;

public final class VoteSerialisations {

    public static final Function<WrappedVote, byte[]> VOTE_SERIALISER = createSerialiserFunction();

    private static Function<WrappedVote, byte[]> createSerialiserFunction() {
        return vote -> {
            return switch (vote.getWalletType()) {
                case KERI -> {
                    val message = vote.getSignature().getBytes();
                    val payload = vote.getPayload().map(String::getBytes).orElse(new byte[0]);
                    val totalLength = message.length + payload.length;

                    val buffer = ByteBuffer.allocate(totalLength);
                    buffer.put(message);
                    buffer.put(payload);

                    yield blake2bHash256(buffer.array());
                }
                case CARDANO -> {
                    val cip30Verifier = new CIP30Verifier(vote.getSignature(), vote.getPublicKey());
                    val verificationResult = cip30Verifier.verify();

                    val bytes = Optional.ofNullable(verificationResult.getMessage()).orElse(new byte[0]);

                    yield blake2bHash256(bytes);
                }
            };
        };
    }

}
