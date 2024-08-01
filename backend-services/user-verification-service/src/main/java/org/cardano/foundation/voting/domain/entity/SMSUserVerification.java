package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.VerificationStatus;
import org.cardano.foundation.voting.domain.WalletType;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_user_verification")
@Slf4j
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class SMSUserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "wallet_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletType walletType;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "phone_number_hash", nullable = false)
    private String phoneNumberHash;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Override
    public String toString() {
        return "SMSUserVerification{" +
                "id='" + id + '\'' +
                ", walletId='" + walletId + '\'' +
                ", walletType='" + walletType + '\'' +
                ", eventId='" + eventId + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", requestId='" + requestId + '\'' +
                ", expiresAt=" + expiresAt +
                ", phoneNumberHash='" + phoneNumberHash + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
