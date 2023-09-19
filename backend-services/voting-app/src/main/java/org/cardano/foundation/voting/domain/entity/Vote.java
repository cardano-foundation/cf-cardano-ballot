package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote")
@Builder
@Slf4j
@ToString(exclude = { "coseSignature", "cosePublicKey"} )
public class Vote extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private String id;

    @Column(name = "id_numeric_hash", nullable = false)
    @Getter
    @Setter
    private long idNumericHash;

    @Column(name = "event_id", nullable = false)
    @Getter
    @Setter
    private String eventId;

    @Column(name = "category_id", nullable = false)
    @Getter
    @Setter
    private String categoryId;

    @Column(name = "proposal_id", nullable = false)
    @Getter
    @Setter
    private String proposalId;

    @Column(name = "voter_stake_address", nullable = false)
    @Getter
    @Setter
    private String voterStakingAddress;

    @Column(name = "cose_signature", nullable = false, columnDefinition = "text", length = 2048)
    @Getter
    @Setter
    private String coseSignature;

    @Column(name = "cose_public_key")
    @Nullable
    private String cosePublicKey;

    @Column(name = "voting_power")
    @Nullable
    // nullable since it makes sense only for STAKE_BASED or BALANCE_BASED events
    private Long votingPower;

    @Column(name = "voted_at_slot", nullable = false)
    @Getter
    @Setter
    private long votedAtSlot;

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

}
