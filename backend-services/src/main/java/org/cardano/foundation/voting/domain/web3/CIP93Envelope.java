package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CIP93Envelope<T> {

    private String uri;
    private String action;
    private String actionText;
    private long slot;
    private T data;

}
