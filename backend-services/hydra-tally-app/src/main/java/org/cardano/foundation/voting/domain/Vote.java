package org.cardano.foundation.voting.domain;

import io.vavr.control.Either;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.zalando.problem.Problem;

import java.util.Optional;
import java.util.UUID;

import static org.cardano.foundation.voting.utils.MoreUUID.isUUIDv4;

public record Vote(
        UUID voteId,
        String eventId,
        UUID categoryId,
        UUID proposalId,
        String voterStakeAddress,
        String coseSignature,
        Optional<String> cosePublicKey) {

    public static Either<Problem, Vote> create(String voteId,
                                               String eventId,
                                               String categoryId,
                                               String proposalId,
                                               String voterStakeAddress,
                                               String coseSignature,
                                               Optional<String> cosePublicKey) {
        var parser = new CIP30Verifier(coseSignature, cosePublicKey);

        var result = parser.verify();

        if (!result.isValid()) {
            var problem = Problem.builder().withTitle("COSE_ERROR").withDetail("Cose signature failed").build();

            return Either.left(problem);
        }

        if (!isUUIDv4(voteId)) {
            var problem = Problem.builder().withTitle("NO_UUID4").withDetail("voteId must be UUID4").build();

            return Either.left(problem);
        }

        if (!isUUIDv4(categoryId)) {
            var problem = Problem.builder().withTitle("NO_UUID4").withDetail("Category must be UUID4").build();

            return Either.left(problem);
        }

        if (!isUUIDv4(proposalId)) {
            var problem = Problem.builder().withTitle("NO_UUID4").withDetail("Proposal must be UUID4").build();

            return Either.left(problem);
        }

        var vote = new Vote(
                UUID.fromString(voteId),
                eventId,
                UUID.fromString(categoryId),
                UUID.fromString(proposalId),
                voterStakeAddress,
                coseSignature,
                cosePublicKey
        );

        return Either.right(vote);
    }

}


