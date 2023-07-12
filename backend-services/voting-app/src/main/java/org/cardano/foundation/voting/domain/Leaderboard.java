package org.cardano.foundation.voting.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class Leaderboard {

    // per category
    @Builder
    @Getter
    public static class ByCategory {

        private String category;
        private Map<String, Votes> proposals;

    }

    @Builder
    @Getter
    public static class ByEvent {

        private String event;
        private long totalVotesCount;
        private long totalVotingPower;

    }

    public record Votes(long votes, long votingPower) { }

}
