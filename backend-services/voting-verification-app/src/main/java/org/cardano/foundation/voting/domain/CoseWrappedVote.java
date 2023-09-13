package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
public class CoseWrappedVote {

    private String coseSignature;

    @Builder.Default
    private Optional<String> cosePublicKey = Optional.empty();

}
