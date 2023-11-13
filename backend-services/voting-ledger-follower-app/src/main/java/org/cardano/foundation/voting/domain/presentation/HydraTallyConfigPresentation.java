package org.cardano.foundation.voting.domain.presentation;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class HydraTallyConfigPresentation {

    private String contractName;

    private String compiledScript;

    private String compiledScriptHash;

    private String contractVersion;

    private String compilerName;

    private String compilerVersion;

    private String plutusVersion;

    @Builder.Default
    private List<String> verificationKeys = new ArrayList<>();

}
