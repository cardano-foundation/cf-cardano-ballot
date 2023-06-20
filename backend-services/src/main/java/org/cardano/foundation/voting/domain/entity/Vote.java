package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.cip30.CIP30Verifier;

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
    @Column(name = "id")
    private String id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "voter_stake_address")
    private String voterStakingAddress;

    @Column(name = "cose_signature")
    private String coseSignature;

    @Column(name = "cose_public_key")
    private String cosePublicKey;

    @Column(name = "voting_power")
    private long votingPower;

    @Column(name = "network")
    private CardanoNetwork network;

    @Column(name = "voted_at_slot")
    private long votedAtSlot;

    private static Function<Vote, byte[]> createSerialiserFunction() {
        return vote -> {
            var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());
            var verificationResult = cip30Verifier.verify();

            return verificationResult.getMessage();
        };
    }

}
