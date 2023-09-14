package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VerificationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "discord_user_verification")
@Slf4j
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
public class DiscordUserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "discord_id_hash", nullable = false)
    private String discordIdHash;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "stake_address", nullable = false)
    private String stakeAddress;

    @Column(name = "secret_code", nullable = false)
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
