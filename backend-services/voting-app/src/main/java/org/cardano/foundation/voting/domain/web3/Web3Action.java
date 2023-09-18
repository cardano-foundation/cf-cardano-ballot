package org.cardano.foundation.voting.domain.web3;

public enum Web3Action {

    CAST_VOTE, // casting vote

    VIEW_VOTE_RECEIPT, // obtaining vote receipt

    LOGIN, // JWT login based on WEB3 login

    IS_VOTE_CASTING_ALLOWED, // checking if vote casting is still allowed

    IS_VOTE_CHANGING_ALLOWED, // checking if vote casting changing

    VOTED_ON // getting list of categories and proposals user already voted on

}
