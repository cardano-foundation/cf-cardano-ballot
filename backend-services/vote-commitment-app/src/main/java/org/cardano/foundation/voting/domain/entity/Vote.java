package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.WalletType;

import javax.annotation.Nullable;
import java.util.Optional;

import static jakarta.persistence.EnumType.STRING;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote")
@Builder
@Slf4j
@ToString(exclude = { "signature", "payload", "publicKey"} )
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

    @Column(name = "wallet_id", nullable = false)
    @Getter
    @Setter
    private String walletId;

    @Column(name = "wallet_type", nullable = false)
    @Getter
    @Setter
    @Enumerated(STRING)
    private WalletType walletType;

    @Column(name = "signature", nullable = false, columnDefinition = "text", length = 2048)
    @Getter
    @Setter
    private String signature;

    @Column(name = "payload", columnDefinition = "text", length = 2048)
    @Nullable // in case of Cardano Wallet, payload is inside of signature
    private String payload;

    @Column(name = "public_key")
    @Nullable
    private String publicKey;

    @Column(name = "voting_power")
    @Nullable
    // nullable since it makes sense only for STAKE_BASED or BALANCE_BASED events
    private Long votingPower;

    @Column(name = "voted_at_slot", nullable = false)
    @Getter
    @Setter
    private long votedAtSlot;

    @Nullable
    public Optional<String> getPublicKey() {
        return Optional.ofNullable(publicKey);
    }

    public void setPublicKey(Optional<String> publicKey) {
        this.publicKey = publicKey.orElse(null);
    }

    public Optional<Long> getVotingPower() {
        return Optional.ofNullable(votingPower);
    }

    public void setVotingPower(Optional<Long> votingPower) {
        this.votingPower = votingPower.orElse(null);
    }

    public void setPayload(Optional<String> payload) {
        this.payload = payload.orElse(null);
    }

    public Optional<String> getPayload() {
        return Optional.ofNullable(payload);
    }

}
