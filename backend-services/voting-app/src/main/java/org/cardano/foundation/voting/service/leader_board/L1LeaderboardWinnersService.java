package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.cardano.foundation.voting.domain.VotingEventType.USER_BASED;
import static org.zalando.problem.Status.*;

@Service
@Qualifier("l1_leaderboard_winners_service")
public class L1LeaderboardWinnersService extends AbstractWinnersService implements LeaderboardWinnersService {

    @Override
    public Either<Problem, Leaderboard.ByProposalsInCategoryStats> getCategoryLeaderboard(String event,
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

        // for now we only support user based voting via Hydra and L1 Leaderboard winners service
        // contract needs to be modified to support vote count and voting power based voting
        if (eventDetails.votingEventType() != USER_BASED) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_EVENT_TYPE_NOT_SUPPORTED")
                    .withDetail("Voting event type not supported, event:" + event + ", votingEventType:" + eventDetails.votingEventType())
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var maybeCategory = eventDetails.categoryDetailsById(category);

        if (maybeCategory.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var categoryDetails = maybeCategory.orElseThrow();

        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Category level voting results not available until results can be revealed!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var votingResultsE = chainFollowerClient.getVotingResults(
                eventDetails.id(),
                categoryDetails.id()
        );

        if (votingResultsE.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("ERROR_GETTING_VOTING_RESULTS")
                    .withDetail("Unable to get voting results from chain-tip follower service, event:" + event + ", category:" + category)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }

        var votingResults = votingResultsE.get();

        return Either.right(Leaderboard.ByProposalsInCategoryStats.builder()
                .category(category)
                .proposals(votingResults.results().entrySet().stream().collect(toMap(Map.Entry::getKey, e -> {
                    var votes = e.getValue();

                    return new Leaderboard.Votes(votes, "0"); // TODO support for voting power from L1 data
                }))).build()
        );

    }

}
