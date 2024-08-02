package org.cardano.foundation.voting.domain;

public record EventAdditionalInfo(String id,
                                  boolean notStarted,
                                  boolean started,
                                  boolean finished,
                                  boolean active,
                                  boolean proposalsReveal,
                                  boolean commitmentsWindowOpen,
                                  boolean userBased
) {

}
