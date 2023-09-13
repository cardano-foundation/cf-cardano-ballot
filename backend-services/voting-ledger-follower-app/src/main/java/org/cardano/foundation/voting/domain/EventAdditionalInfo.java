package org.cardano.foundation.voting.domain;

public record EventAdditionalInfo(String id,
                                  boolean notStarted,
                                  boolean finished,
                                  boolean active,
                                  boolean proposalsReveal) {
}
