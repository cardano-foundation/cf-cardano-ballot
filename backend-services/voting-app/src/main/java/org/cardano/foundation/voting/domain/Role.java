package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.domain.web3.Web3Action;

import java.util.List;

import static org.cardano.foundation.voting.domain.web3.Web3Action.IS_VOTE_CASTING_ALLOWED;
import static org.cardano.foundation.voting.domain.web3.Web3Action.VIEW_VOTE_RECEIPT;

public enum Role {

    VOTER(List.of(VIEW_VOTE_RECEIPT, IS_VOTE_CASTING_ALLOWED));

    private final List<Web3Action> allowedActions;

    Role(List<Web3Action> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public List<Web3Action> allowedActions() {
        return allowedActions;
    }

}
