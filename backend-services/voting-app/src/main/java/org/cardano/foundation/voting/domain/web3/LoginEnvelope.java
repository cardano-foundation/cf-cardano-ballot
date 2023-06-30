package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginEnvelope {

    private String event;
    private String network;
    private String role;

}
