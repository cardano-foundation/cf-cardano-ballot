package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.repository.CustomVoteRepository;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.zalando.problem.Status.*;

@Service
@Qualifier("db_leaderboard_winners_service")
public class DBLeaderboardWinnersService extends AbstractWinnersService implements LeaderboardWinnersService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CustomVoteRepository customVoteRepository;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Leaderboard.ByProposalsInCategoryStats> getCategoryLeaderboard(String event, String category, boolean forceLeaderboard) {
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

        var votes = voteRepository.getCategoryLevelStats(event, categoryDetails.id());

        Map<String, Leaderboard.Votes> proposalResultsMap = votes.stream()
                .collect(toMap(VoteRepository.CategoryLevelStats::getProposalId, v -> {
                    var totalVotesCount = Optional.ofNullable(v.getTotalVoteCount()).orElse(0L);
                    var totalVotingPower = Optional.ofNullable(v.getTotalVotingPower()).map(String::valueOf).orElse("0");

                    var b = Leaderboard.Votes.builder();
                    b.votes(totalVotesCount);

                    switch (eventDetails.votingEventType()) {
                        case BALANCE_BASED, STAKE_BASED -> b.votingPower(totalVotingPower);
                    }

                    return b.build();
                }));

        var proposalResults = calcProposalsResults(categoryDetails, proposalResultsMap, eventDetails);

        return Either.right(Leaderboard.ByProposalsInCategoryStats.builder()
                .category(categoryDetails.id())
                .proposals(proposalResults)
                .build()
        );
    }

    @Override
    @Transactional(readOnly = true, isolation = SERIALIZABLE)
    public Either<Problem, List<Leaderboard.WinnerStats>> getEventWinners(String event, boolean forceLeaderboard) {
        var eventDetailsE = chainFollowerClient.getEventDetails(event);

        if (eventDetailsE.isEmpty()) {
            return Either.left(eventDetailsE.getLeft());
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

        var categoryIds = eventDetails.categories()
                .stream()
                .map(ChainFollowerClient.CategoryDetailsResponse::id)
                .toList();

        return Either.right(customVoteRepository.getEventWinners(event, categoryIds));
    }

    private static HashMap<String, Leaderboard.Votes> calcProposalsResults(ChainFollowerClient.CategoryDetailsResponse categoryDetails,
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
