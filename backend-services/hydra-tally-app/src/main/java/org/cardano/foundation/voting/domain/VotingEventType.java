package org.cardano.foundation.voting.domain;

public enum VotingEventType {

    USER_BASED, // 1 person 1 vote

    STAKE_BASED, // 1 ADA = 1 vote but voter must stake it

    BALANCE_BASED // 1 ADA = 1 vote but voter don't have to stake it

}
