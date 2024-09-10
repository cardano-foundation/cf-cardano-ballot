package org.cardano.foundation.voting.domain;

import lombok.val;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardanofoundation.cip30.CIP30Verifier;

import java.util.Optional;
import java.util.function.Function;

import static com.bloxbean.cardano.client.crypto.Blake2bUtil.blake2bHash256;

public final class VoteSerialisations {

    public static final Function<VoteRepository.CompactVote, byte[]> VOTE_SERIALISER = createSerialiserFunction();

    public static Function<VoteRepository.CompactVote, byte[]> createSerialiserFunction() {
        return vote -> switch (vote.getWalletType()) {
            case CARDANO -> {
                val cip30Verifier = new CIP30Verifier(vote.getSignature(), vote.getPublicKey());
                val verificationResult = cip30Verifier.verify();

                val bytes = Optional.ofNullable(verificationResult.getMessage()).orElse(new byte[0]);

                yield blake2bHash256(bytes);
            }
            case KERI -> {
                val message = vote.getSignature().getBytes();
                val payload = vote.getPayload().map(String::getBytes).orElse(new byte[0]);

                val result = new byte[message.length + payload.length];

                System.arraycopy(message, 0, result, 0, payload.length);

                yield blake2bHash256(result);
            }
        };
    }

}
