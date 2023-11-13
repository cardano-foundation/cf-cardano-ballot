package org.cardano.foundation.voting.service.leader_board;

import com.google.common.collect.Iterables;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DBHighLevelLeaderBoardService implements HighLevelLeaderBoardService {

    private final ChainFollowerClient chainFollowerClient;

    private final VoteRepository voteRepository;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Boolean> isHighLevelEventLeaderboardAvailable(String event,
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

        return isHighLevelEventLeaderboardAvailable(eventDetails, forceLeaderboard);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Boolean> isHighLevelCategoryLeaderboardAvailable(String event, boolean forceLeaderboard) {
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

        return isHighLevelCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
    }

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, Leaderboard.ByEventStats> getEventLeaderboard(String event, boolean forceLeaderboard) {
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

        var highLevelEventLeaderboardAvailableE = isHighLevelEventLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (highLevelEventLeaderboardAvailableE.isEmpty()) {
            return Either.left(highLevelEventLeaderboardAvailableE.getLeft());
        }
        var isHighLevelEventLeaderBoardAvailable = highLevelEventLeaderboardAvailableE.get();

        if (!isHighLevelEventLeaderBoardAvailable) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Event level voting results not available until voting event finishes!")
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var votes = voteRepository.getHighLevelEventStats(event);
        if (votes.isEmpty()) {
            Leaderboard.ByEventStats.ByEventStatsBuilder byEventStatsBuilder = Leaderboard.ByEventStats.builder()
                    .event(eventDetails.id())
                    .totalVotesCount(0L);

            switch (eventDetails.votingEventType()) {
                case BALANCE_BASED, STAKE_BASED -> byEventStatsBuilder.totalVotingPower("0");
            }

            return Either.right(byEventStatsBuilder
                    .build());
        }

        var eventVoteCount = Iterables.getOnlyElement(votes);
        var voteCount = eventVoteCount.getTotalVoteCount();

        var byEventStatsBuilder = Leaderboard.ByEventStats.builder()
                .event(eventDetails.id())
                .totalVotesCount(Optional.ofNullable(voteCount).orElse(0L));

        var votingPower = eventVoteCount.getTotalVotingPower();

        switch (eventDetails.votingEventType()) {
            case BALANCE_BASED, STAKE_BASED ->
                    byEventStatsBuilder.totalVotingPower(Optional.ofNullable(votingPower).map(String::valueOf).orElse("0"));
        }

        var eventLeaderboardAvailableE = isHighLevelCategoryLeaderboardAvailable(eventDetails, forceLeaderboard);
        if (eventLeaderboardAvailableE.isEmpty()) {
            return Either.left(eventLeaderboardAvailableE.getLeft());
        }
        var isEventLeaderBoardAvailable = eventLeaderboardAvailableE.get();

        if (!isEventLeaderBoardAvailable) {
            return Either.right(byEventStatsBuilder.build());
        }

        var allHighLevelCategoryStats = voteRepository.getHighLevelCategoryLevelStats(event);

        var byCategoryStats = allHighLevelCategoryStats.stream()
                .map(categoryLevelStats -> {
                    var byCategoryStatsBuilder = Leaderboard.ByCategoryStats.builder();

                    byCategoryStatsBuilder.id(categoryLevelStats.getCategoryId());
                    byCategoryStatsBuilder.votes(Optional.ofNullable(categoryLevelStats.getTotalVoteCount()).orElse(0L));

                    switch (eventDetails.votingEventType()) {
                        case BALANCE_BASED, STAKE_BASED ->
                                byCategoryStatsBuilder.votingPower(Optional.ofNullable(categoryLevelStats.getTotalVotingPower()).map(String::valueOf).orElse("0"));
                    }

                    return byCategoryStatsBuilder.build();
                })
                .toList();

        var byCategoryStatsMap = byCategoryStats.stream()
                .collect(toMap(Leaderboard.ByCategoryStats::getId, stats -> stats));

        var byCategoryStatsCopy = new ArrayList<>(byCategoryStats);
        // pre init with empty if category not returned from db
        eventDetails.categories().forEach(categoryDetails -> {
            if (!byCategoryStatsMap.containsKey(categoryDetails.id())) {
                var b = Leaderboard.ByCategoryStats.builder();
                b.id(categoryDetails.id());

                b.votes(0L);

                switch (eventDetails.votingEventType()) {
                    case BALANCE_BASED, STAKE_BASED -> b.votingPower("0");
                }

                byCategoryStatsCopy.add(b.build());
            }
        });

        byEventStatsBuilder.categories(byCategoryStatsCopy);

        return Either.right(byEventStatsBuilder.build());
    }


    private Either<Problem, Boolean> isHighLevelEventLeaderboardAvailable(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                          boolean forceLeaderboard) {
        if (forceLeaderboard) {
            return Either.right(true);
        }

        if (eventDetails.highLevelEventResultsWhileVoting()) {
            return Either.right(true);
        }

        return Either.right(eventDetails.proposalsReveal());
    }

    private Either<Problem, Boolean> isHighLevelCategoryLeaderboardAvailable(ChainFollowerClient.EventDetailsResponse eventDetails,
                                                                             boolean forceLeaderboard) {
        if (forceLeaderboard) {
            return Either.right(true);
        }

        if (eventDetails.highLevelCategoryResultsWhileVoting()) {
            return Either.right(true);
        }

        return Either.right(eventDetails.proposalsReveal());
    }

}
