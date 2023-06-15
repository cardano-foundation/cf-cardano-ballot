package org.cardano.foundation.voting.domain.web3.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// CIP-93 -> https://github.com/cardano-foundation/CIPs/pull/442/files

@Getter
@Setter
public abstract class SignedWeb3Request {

    @NotNull
    protected String coseSignature;

    @NotNull
    protected String cosePublicKey;

}
