package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.HashMap;
import java.util.Map;

import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

public class AbstractWinnersService {

    @Autowired
    protected ChainFollowerClient chainFollowerClient;


    @Transactional(readOnly = true)
    public Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event,
                                                                   boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);
        if (eventDetailsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var maybeEventDetails = eventDetailsE.get();
        if (maybeEventDetails.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var eventDetails = maybeEventDetails.orElseThrow();

        return isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
    }

    @Transactional(readOnly = true)
    public Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event,
                                                                   String category,
                                                                   boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);
        if (eventDetailsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_EVENT_DETAILS")
                    .withDetail("Unable to get event details from chain-tip follower service, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var maybeEventDetails = eventDetailsE.get();
        if (maybeEventDetails.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var eventDetails = maybeEventDetails.orElseThrow();

        var maybeCategory = eventDetails.categoryDetailsById(category);

        if (maybeCategory.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        return isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
    }

    protected Either<Problem, Boolean> isCategoryLeaderboardAvailable(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                    boolean forceLeaderboard) {
        if (forceLeaderboard) {
            return Either.right(true);
        }

        if (eventDetails.categoryResultsWhileVoting()) {
            return Either.right(true);
        }

        return Either.right(eventDetails.proposalsReveal());
    }

    protected static Map<String, Leaderboard.Votes> reInitialiseResultsToEmptyIfMissing(ChainFollowerClient.CategoryDetailsResponse categoryDetails,
                                                                                        Map<String, Leaderboard.Votes> proposalResultsMap,
                                                                                        ChainFollowerClient.EventDetailsResponse eventDetails) {
        var categoryProposals = categoryDetails.proposals();

        var proposalResultsMapCopy = new HashMap<>(proposalResultsMap);

        categoryProposals.forEach(proposalDetails -> {
            if (!proposalResultsMap.containsKey(proposalDetails.id())) {
                var b = Leaderboard.Votes.builder();

                b.votes(0L);

                switch (eventDetails.votingEventType()) {
                    case BALANCE_BASED, STAKE_BASED -> b.votingPower("0");
                }

                proposalResultsMapCopy.put(proposalDetails.id(), b.build());
            }
        });

        return proposalResultsMapCopy;
    }

}
