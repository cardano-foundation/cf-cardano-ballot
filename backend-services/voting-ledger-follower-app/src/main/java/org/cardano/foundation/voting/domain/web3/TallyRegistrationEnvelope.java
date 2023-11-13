package org.cardano.foundation.voting.domain.web3;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.cardano.foundation.voting.domain.entity.Tally;

@Getter
@Builder
@ToString
public class TallyRegistrationEnvelope {

    private String name;

    private String description;

    private Tally.TallyType type;

    private Object config;

}
