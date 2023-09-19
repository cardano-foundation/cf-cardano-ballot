package org.cardano.foundation.voting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class Leaderboard {

    @Builder
    @Getter
    public static class WinnerStats {

        private Map<String, String> winners;

    }



    // per category
    @Builder
    @Getter
    public static class ByProposalsInCategoryStats {

        private String category;
        private Map<String, Votes> proposals;

    }

    @Builder
    @Getter
    public static class ByEventStats {

        private String event;
        private long totalVotesCount;
        private String totalVotingPower;
        private List<ByCategoryStats> categories;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ByCategoryStats {
        private String id;
        private long votes;
        private String votingPower;
    }

    @Getter
    @Builder
    public static class Votes {
        private long votes;
        private String votingPower;
    }

}
