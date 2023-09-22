package org.cardano.foundation.voting.domain.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProposalId implements Serializable {

    private String categoryId;

    private String proposalId;

}
