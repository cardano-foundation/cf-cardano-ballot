package org.cardano.foundation.voting.domain;

import org.cardano.foundation.voting.domain.web3.Web3Action;

import java.util.List;
import java.util.stream.Stream;

import static org.cardano.foundation.voting.domain.web3.Web3Action.*;

public enum Role {

    VOTER(List.of(VIEW_VOTE_RECEIPT, IS_VOTE_CASTING_ALLOWED, VOTED_ON));

    private final List<Web3Action> allowedActions;

    Role(List<Web3Action> allowedActions) {
        this.allowedActions = allowedActions;
    }

    public static String supportedRoles() {
        return String.join(", ", Stream.of(Role.values()).map(Role::name).toList());
    }

    public List<Web3Action> allowedActions() {
        return allowedActions;
    }

}
