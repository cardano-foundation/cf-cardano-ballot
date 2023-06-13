package org.cardano.foundation.voting.domain.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
@Builder
public class CategoryReference {

    private String id;

    private String name;

    private String presentationName;

    @Nullable
    private String description;

    private List<ProposalReference> proposals;


}
