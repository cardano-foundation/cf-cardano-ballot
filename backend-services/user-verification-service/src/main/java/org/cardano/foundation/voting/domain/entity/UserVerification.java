package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "user_verification")
@Slf4j
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class UserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "stake_address", nullable = false)
    private String stakeAddress;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "phone_number_hash")
    private String phoneNumberHash;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.NOT_REQUESTED;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private Channel channel;

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

    @Override
    public String toString() {
        return "UserVerification{" +
                "stakeAddress='" + stakeAddress + '\'' +
                ", eventId='" + eventId + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", requestId='" + requestId + '\'' +
                ", phoneNumberHash='" + phoneNumberHash + '\'' +
                ", status=" + status +
                ", provider=" + provider +
                ", channel=" + channel +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
