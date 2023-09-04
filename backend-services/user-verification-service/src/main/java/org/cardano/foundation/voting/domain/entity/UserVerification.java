package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "user_verification")
@Slf4j
@SuperBuilder
@NoArgsConstructor
public class UserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private String id;

    @Column(name = "stake_address", nullable = false)
    @Getter
    @Setter
    private String stakeAddress;

    @Column(name = "event_id", nullable = false)
    @Getter
    @Setter
    private String eventId;

    @Column(name = "verification_code", nullable = false)
    @Getter
    @Setter
    private String verificationCode;

    @Column(name = "request_id", nullable = false)
    @Getter
    @Setter
    private String requestId;

    @Column(name = "phone_number_hash")
    @Nullable
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
        SMS,
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

    public Optional<String> getPhoneNumberHash() {
        return Optional.ofNullable(phoneNumberHash);
    }

    public void setPhoneNumberHash(Optional<String> phoneNumberHash) {
        this.phoneNumberHash = phoneNumberHash.orElse(null);
    }

    @Override
    public String toString() {
        return "UserVerification{" +
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
