package org.cardano.foundation.voting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class HydraTallyConfig {

    private final String contractName;
    private final String contractDescription;
    private final String contractVersion;

    private final String compiledScript;
    private final String compiledScriptHash;

    private final String compilerName;
    private final String compilerVersion;

    // verification key hashes
    private final List<String> partiesVerificationKeys;

    private final String plutusVersion;

}
