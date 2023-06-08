package org.cardano.foundation.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// CIP-93 -> https://github.com/cardano-foundation/CIPs/pull/442/files

@Getter
@Setter
public abstract class Web3Request {

    @NotNull
    protected String uri;

    @NotNull
    protected Web3Action action;

    @NotNull
    protected String actionText;

    @NotNull
    protected long slot;

}
