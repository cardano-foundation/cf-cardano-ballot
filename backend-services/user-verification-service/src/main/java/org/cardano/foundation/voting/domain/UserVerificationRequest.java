package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@Setter
@ToString
public class UserVerificationRequest {

    private String stakeAddress;

    private String phoneNumber;

}
