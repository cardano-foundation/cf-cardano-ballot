package org.cardano.foundation.voting.domain.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.utils.Enums;

import java.util.Optional;

@Builder
@Getter
public class CIP93Envelope<T> {

    private String uri;
    private String action;
    private String actionText;
    private String slot;
    private T data;

    public long getSlotAsLong() {
        return Long.parseLong(slot);
    }

    public Optional<Web3Action> getActionAsEnum() {
        return Enums.getIfPresent(Web3Action.class, action);
    }

}
