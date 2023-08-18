package org.cardano.foundation.voting.service.leader_board;

import com.google.common.collect.Iterables;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.zalando.problem.Status.*;

@Service
@Slf4j
public class DefaultLeaderBoardService implements LeaderBoardService {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private Environment env;

    @Override
    public Either<Problem, Leaderboard.ByEvent> getEventLeaderboard(String event) {
        var maybeEvent = referenceDataService.findValidEventByName(event);
        if (maybeEvent.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var e = maybeEvent.orElseThrow();

        if (!isDevEnv() && !expirationService.isEventFinished(e) && !e.isHighLevelResultsWhileVoting()) {
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
                .event(e.getId())
                .totalVotesCount(Optional.ofNullable(voteCount).orElse(0L))
                .totalVotingPower(Optional.ofNullable(votingPower).map(String::valueOf).orElse("0"))
                .build()
        );
    }

    @Override
    public Either<Problem, Leaderboard.ByCategory> getCategoryLeaderboard(String event, String category) {
        var maybeEvent = referenceDataService.findValidEventByName(event);
        if (maybeEvent.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_EVENT")
                    .withDetail("Unrecognised event, event:" + event)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        var e = maybeEvent.orElseThrow();

        var maybeCategory = e.findCategoryByName(category);

        if (maybeCategory.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("UNRECOGNISED_CATEGORY")
                    .withDetail("Unrecognised category, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }
        var c = maybeCategory.orElseThrow();

        if (!isDevEnv() && !expirationService.isEventFinished(e) && !e.isCategoryResultsWhileVoting()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Voting results not available until event finishes, category:" + category)
                    .withStatus(FORBIDDEN)
                    .build()
            );
        }

        var votes = voteRepository.countAllByEventId(event, c.getId());

        var proposalResults = Map.<String, Leaderboard.Votes>of();
        if (c.isGdprProtection()) {
            proposalResults = votes.stream()
                    .collect(toMap(VoteRepository.EventCategoryVoteCount::getProposalId, v -> {
                        var totalVotesCount = Optional.ofNullable(v.getTotalVoteCount()).orElse(0L);
                        var totalVotingPower = Optional.ofNullable(v.getTotalVotingPower()).map(String::valueOf).orElse("0");

                        return new Leaderboard.Votes(totalVotesCount, totalVotingPower);
                    }));
        } else {
            proposalResults = votes.stream()
                    .collect(toMap(VoteRepository.EventCategoryVoteCount::getProposalName, v -> {
                        var totalVotesCount = Optional.ofNullable(v.getTotalVoteCount()).orElse(0L);
                        var totalVotingPower = Optional.ofNullable(v.getTotalVotingPower()).map(String::valueOf).orElse("0");

                        return new Leaderboard.Votes(totalVotesCount, totalVotingPower);
                    }));
        }

        return Either.right(Leaderboard.ByCategory.builder()
                .category(c.getId())
                .proposals(proposalResults)
                .build()
        );
    }

    private boolean isDevEnv() {
        return Arrays.stream(env.getActiveProfiles()).anyMatch(env -> env.contains("dev"));
    }

}
