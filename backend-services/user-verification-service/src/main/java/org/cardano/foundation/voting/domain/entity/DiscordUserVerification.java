package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VerificationStatus;
import org.cardano.foundation.voting.domain.WalletType;

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

    @Column(name = "wallet_id")
    @Getter
    @Setter
    @Nullable
    private String walletId;

    @Column(name = "wallet_type")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private WalletType walletType;

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

    @Override
    public String toString() {
        return "DiscordUserVerification{" +
                "discordIdHash='" + discordIdHash + '\'' +
                ", walletId='" + walletId + '\'' +
                ", walletType='" + walletType + '\'' +
                ", eventId='" + eventId + '\'' +
                ", verificationCode='" + secretCode + '\'' +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public Optional<String> getWalletId() {
        return Optional.ofNullable(walletId);
    }

    public void setWalletId(Optional<String> walletId) {
        this.walletId = walletId.orElse(null);
    }

}
