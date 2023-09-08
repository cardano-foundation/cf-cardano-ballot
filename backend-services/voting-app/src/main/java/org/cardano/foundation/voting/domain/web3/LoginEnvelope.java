package org.cardano.foundation.voting.domain.web3;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginEnvelope {

    private String event;

    private String address;

    private String network;

    private String role;

}
