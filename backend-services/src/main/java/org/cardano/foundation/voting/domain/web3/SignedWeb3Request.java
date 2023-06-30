package org.cardano.foundation.voting.domain.web3;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// CIP-93 -> https://github.com/cardano-foundation/CIPs/pull/442/files

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SignedWeb3Request {

    @NotNull
    protected String coseSignature;

    protected String cosePublicKey;

}
