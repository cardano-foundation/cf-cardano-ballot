package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class CreateCategoryCommand {

    private String name;

    private String event;

    @Builder.Default
    private boolean gdprProtection = false;

    @Builder.Default
    private List<Proposal> proposals = List.of();

    @Builder.Default
    private SchemaVersion schemaVersion = SchemaVersion.V1;

}
