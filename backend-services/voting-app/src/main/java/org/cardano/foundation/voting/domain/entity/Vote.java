package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.cip30.CIP30Verifier;

import javax.annotation.Nullable;
import java.util.function.Function;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "vote")
public class Vote extends AbstractTimestampEntity {

    public static final Function<Vote, byte[]> VOTE_SERIALISER = createSerialiserFunction();

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(name = "proposal_id", nullable = false)
    private String proposalId;

    @Column(name = "voter_stake_address", nullable = false)
    private String voterStakingAddress;

    @Column(name = "cose_signature", nullable = false, columnDefinition = "text", length = 2048)
    private String coseSignature;

    @Column(name = "cose_public_key", nullable = false)
    private String cosePublicKey;

    @Column(name = "voting_power")
    @Nullable
    private Long votingPower; // makes sense only for STAKE_BASED or BALANCE_BASED events

    @Column(name = "network", nullable = false)
    private CardanoNetwork network;

    @Column(name = "voted_at_slot", nullable = false)
    private long votedAtSlot;

    private static Function<Vote, byte[]> createSerialiserFunction() {
        return vote -> {
            var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());
            var verificationResult = cip30Verifier.verify();

            return verificationResult.getMessage();
        };
    }

}
