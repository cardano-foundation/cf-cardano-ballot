package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BlockchainData {

    private long absoluteSlot;

    private int epochNo;

}
