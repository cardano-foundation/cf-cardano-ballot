package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VerificationStatus;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "discord_user_verification")
@Slf4j
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class DiscordUserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private String discordIdHash;

    @Column(name = "event_id", nullable = false)
    @Getter
    @Setter
    private String eventId;

    @Column(name = "stake_address")
    @Nullable
    private String stakeAddress;

    @Column(name = "secret_code", nullable = false)
    @Getter
    @Setter
    private String secretCode;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private VerificationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    @Getter
    @Setter
    private LocalDateTime expiresAt;

    public Optional<String> getStakeAddress() {
        return Optional.ofNullable(stakeAddress);
    }

    public void setStakeAddress(Optional<String> stakeAddress) {
        this.stakeAddress = stakeAddress.orElse(null);
    }

    @Override
    public String toString() {
        return "DiscordUserVerification{" +
                "discordIdHash='" + discordIdHash + '\'' +
                ", stakeAddress='" + stakeAddress + '\'' +
                ", eventId='" + eventId + '\'' +
                ", verificationCode='" + secretCode + '\'' +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
