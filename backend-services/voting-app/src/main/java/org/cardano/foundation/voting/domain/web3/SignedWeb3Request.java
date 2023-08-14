package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Optional;

// CIP-93 -> https://github.com/cardano-foundation/CIPs/pull/442/files

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class SignedWeb3Request {

    @NotBlank
    protected String coseSignature;

    protected Optional<@NotBlank String> cosePublicKey;

}
