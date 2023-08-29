package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteVerificationResult {

    private boolean isVerified;

    private CardanoNetwork network;

}
