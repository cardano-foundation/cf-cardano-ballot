package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FullMetadataScanEnvelope {

    @NotBlank
    private String address;
    @NotBlank
    private String network;

}
