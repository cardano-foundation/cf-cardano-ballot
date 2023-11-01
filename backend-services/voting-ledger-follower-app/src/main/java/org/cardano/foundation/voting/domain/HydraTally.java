package org.cardano.foundation.voting.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HydraTally {

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__contract_name", nullable = false)
    private String contractName;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__contract_description")
    @Nullable
    private String contractDescription;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__contract_version", nullable = false)
    private String contractVersion;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__compiled_script", nullable = false, columnDefinition = "text", length = 16000)
    private String compiledScript;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__compiled_script_hash", nullable = false)
    private String compiledScriptHash;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__compiler_name", nullable = false)
    private String compilerName;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__compiler_version", nullable = false)
    private String compilerVersion;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__plutus_version", nullable = false)
    private String plutusVersion;

    @Getter
    @Setter
    @Column(name = "hydra_tally_config__verification_keys", nullable = false, columnDefinition = "text", length = 1024)
    // comma separated list of blake224 hashes of the verification keys
    private String verificationKeys;

    public List<String> getVerificationKeysAsList() {
        return Arrays.asList(verificationKeys.split(":"));
    }

    public void setDescription(Optional<String> description) {
        this.contractDescription = description.orElse(null);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(contractDescription);
    }

}
