package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class HydraTallyRegistrationEnvelope {

    private String contractName;

    private String contractDesc;

    private String contractVersion;

    private String compiledScript;

    private String compiledScriptHash;

    private String compilerName;

    private String compilerVersion;

    private String plutusVersion;

    private List<String> verificationKeys;

}
