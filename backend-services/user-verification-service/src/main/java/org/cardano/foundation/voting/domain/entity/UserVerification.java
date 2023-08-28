package org.cardano.foundation.voting.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_verification")
@Builder
@Slf4j
public class UserVerification extends AbstractTimestampEntity {

    @Id
    @Column(name = "stake_address", nullable = false)
    @Getter
    @Setter
    private String stakeAddress;

    @Column(name = "event_id", nullable = false)
    @Getter
    @Setter
    private String eventId;

    @Column(name = "phone_number")
    @Nullable
    private String phoneNumber;

    @Column(name = "status", nullable = false)
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_REQUESTED;

    @Column(name = "provider", nullable = false)
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "channel", nullable = false)
    @Getter
    @Setter
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
        TWILIO
    }

    public Optional<String> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public void setPhoneNumber(Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber.orElse(null);
    }

}
