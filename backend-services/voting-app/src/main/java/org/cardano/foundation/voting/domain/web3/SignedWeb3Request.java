package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

// CIP-93 -> https://github.com/cardano-foundation/CIPs/pull/442/files

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SignedWeb3Request {

    @NotBlank
    protected String coseSignature;

    protected Optional<@NotBlank String> cosePublicKey;

}
