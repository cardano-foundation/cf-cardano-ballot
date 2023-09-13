package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_user_verification")
@Slf4j
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class DiscordUserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "stake_address", nullable = false)
    private String stakeAddress;

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
    @Builder.Default
    @Getter
    @Setter
    private Status status = Status.NOT_REQUESTED;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Provider provider;

    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Channel channel;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    @Builder.Default
    @Getter
    @Setter
    private LocalDateTime expiresAt = LocalDateTime.now(); // TODO clock

    public enum Channel {
        SMS
    }

    public enum Status {
        NOT_REQUESTED,
        PENDING,
        VERIFIED,
    }

    public enum Provider {
        TWILIO,
        AWS_SNS
    }

    @Override
    public String toString() {
        return "SMSUserVerification{" +
                "id='" + id + '\'' +
                ", stakeAddress='" + stakeAddress + '\'' +
                ", eventId='" + eventId + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", requestId='" + requestId + '\'' +
                ", expiresAt=" + expiresAt +
                ", phoneNumberHash='" + phoneNumberHash + '\'' +
                ", status=" + status +
                ", provider=" + provider +
                ", channel=" + channel +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
