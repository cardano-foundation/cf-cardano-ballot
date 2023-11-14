package org.cardano.foundation.voting.domain.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.cardano.foundation.voting.domain.HydraTally;

import java.util.Optional;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Tally extends AbstractTimestampEntity {

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    @Nullable
    private String description;

    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TallyType type;

    @Getter
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "contract_name", column = @Column(name = "hydra_tally_config__contract_name")),
        @AttributeOverride(name = "contract_description", column = @Column(name = "hydra_tally_config__contract_description")),
        @AttributeOverride(name = "contract_version", column = @Column(name = "hydra_tally_config__contract_version")),
        @AttributeOverride(name = "compiled_script", column = @Column(name = "hydra_tally_config__compiled_script")),
        @AttributeOverride(name = "compiled_script_hash", column = @Column(name = "hydra_tally_config__compiled_script_hash")),
        @AttributeOverride(name = "compiler_name", column = @Column(name = "hydra_tally_config__compiler_name")),
        @AttributeOverride(name = "compiler_version", column = @Column(name = "hydra_tally_config__compiler_version")),
        @AttributeOverride(name = "plutus_version", column = @Column(name = "hydra_tally_config__plutus_version")),
    })
    private HydraTally hydraTallyConfig;

    public enum TallyType {
        HYDRA
    }

    public void setDescription(Optional<String> description) {
        this.description = description.orElse(null);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

}
