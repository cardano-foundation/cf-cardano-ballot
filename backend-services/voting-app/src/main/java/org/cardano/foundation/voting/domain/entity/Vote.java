package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.cardanofoundation.cip30.CIP30Verifier;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote")
@Builder
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

    @Column(name = "cose_public_key")
    @Nullable
    private String cosePublicKey;

    @Column(name = "voting_power")
    @Nullable
    // makes sense only for STAKE_BASED or BALANCE_BASED events
    private Long votingPower;

    @Column(name = "voted_at_slot", nullable = false)
    private long votedAtSlot;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getVoterStakingAddress() {
        return voterStakingAddress;
    }

    public void setVoterStakingAddress(String voterStakingAddress) {
        this.voterStakingAddress = voterStakingAddress;
    }

    public String getCoseSignature() {
        return coseSignature;
    }

    public void setCoseSignature(String coseSignature) {
        this.coseSignature = coseSignature;
    }

    @Nullable
    public Optional<String> getCosePublicKey() {
        return Optional.ofNullable(cosePublicKey);
    }

    public void setCosePublicKey(Optional<String> cosePublicKey) {
        this.cosePublicKey = cosePublicKey.orElse(null);
    }

    public Optional<Long> getVotingPower() {
        return Optional.ofNullable(votingPower);
    }

    public void setVotingPower(Optional<Long> votingPower) {
        this.votingPower = votingPower.orElse(null);
    }

    public long getVotedAtSlot() {
        return votedAtSlot;
    }

    public void setVotedAtSlot(long votedAtSlot) {
        this.votedAtSlot = votedAtSlot;
    }

    private static Function<Vote, byte[]> createSerialiserFunction() {
        return vote -> {
            var cip30Verifier = new CIP30Verifier(vote.getCoseSignature(), vote.getCosePublicKey());
            var verificationResult = cip30Verifier.verify();

            return verificationResult.getMessage();
        };
    }

}
