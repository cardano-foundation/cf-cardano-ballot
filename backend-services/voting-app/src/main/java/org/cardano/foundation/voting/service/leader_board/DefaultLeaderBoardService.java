package org.cardano.foundation.voting.service.leader_board;

import com.google.common.collect.Iterables;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
public class DefaultLeaderBoardService implements LeaderBoardService {

    @Autowired
    private ChainFollowerClient chainFollowerClient;

    @Autowired
    private VoteRepository voteRepository;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @Override
    public Either<Problem, Boolean> isEventLeaderboardAvailable(String event) {
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

        return isEventLeaderboardAvailable(eventDetails);
    }

    private Either<Problem, Boolean> isEventLeaderboardAvailable(ChainFollowerClient.EventDetailsResponse eventDetails) {
        if (forceLeaderboardResultsAvailability) {
            return Either.right(true);
        }

        if (eventDetails.highLevelResultsWhileVoting()) {
            return Either.right(true);
        }

        return Either.right(eventDetails.finished());
    }

    @Override
    public Either<Problem, Leaderboard.ByEvent> getEventLeaderboard(String event) {
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

        var eventLeaderboardAvailableE = isEventLeaderboardAvailable(eventDetails);
        if (eventLeaderboardAvailableE.isEmpty()) {
            return Either.left(eventLeaderboardAvailableE.getLeft());
        }
        var isEventLeaderBoardAvailable = eventLeaderboardAvailableE.get();

        if (!isEventLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("High level voting results not available until voting event finishes.")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var votes = voteRepository.countAllByEventId(event);
        if (votes.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("INTERNAL_ERROR")
                    .withDetail("No votes for event, event:" + event)
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );
        }
        var eventVoteCount = Iterables.getOnlyElement(votes);
        var votingPower = eventVoteCount.getTotalVotingPower();
        var voteCount = eventVoteCount.getTotalVoteCount();

        return Either.right(Leaderboard.ByEvent.builder()
                .event(eventDetails.id())
                .totalVotesCount(Optional.ofNullable(voteCount).orElse(0L))
                .totalVotingPower(Optional.ofNullable(votingPower).map(String::valueOf).orElse("0"))
                .build()
        );
    }

    @Override
    public Either<Problem, Leaderboard.ByCategory> getCategoryLeaderboard(String event, String category) {
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
        var categoryDetails = maybeCategory.orElseThrow();

        var categoryLeaderboardAvailableE = isCategoryLeaderboardAvailable(eventDetails, categoryDetails);
        if (categoryLeaderboardAvailableE.isEmpty()) {
            return Either.left(categoryLeaderboardAvailableE.getLeft());
        }

        var isCategoryLeaderBoardAvailable = categoryLeaderboardAvailableE.get();
        if (!isCategoryLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("High level voting results not available until voting event finishes.")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var votes = voteRepository.countAllByEventId(event, categoryDetails.id());

        var proposalResults = votes.stream()
                .collect(toMap(VoteRepository.EventCategoryVoteCount::getProposalId, v -> {
                    var totalVotesCount = Optional.ofNullable(v.getTotalVoteCount()).orElse(0L);
                    var totalVotingPower = Optional.ofNullable(v.getTotalVotingPower()).map(String::valueOf).orElse("0");

                    return new Leaderboard.Votes(totalVotesCount, totalVotingPower);
                }));

        return Either.right(Leaderboard.ByCategory.builder()
                .category(categoryDetails.id())
                .proposals(proposalResults)
                .build()
        );
    }

    @Override
    public Either<Problem, Boolean> isCategoryLeaderboardAvailable(String event, String category) {
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
        var categoryDetails = maybeCategory.orElseThrow();

        return isCategoryLeaderboardAvailable(eventDetails, categoryDetails);
    }

    private Either<Problem, Boolean> isCategoryLeaderboardAvailable(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                    ChainFollowerClient.CategoryDetailsResponse categoryDetails) {
        if (forceLeaderboardResultsAvailability) {
            return Either.right(true);
        }

        if (eventDetails.categoryResultsWhileVoting()) {
            return Either.right(true);
        }

        return Either.right(eventDetails.finished());
    }

}
