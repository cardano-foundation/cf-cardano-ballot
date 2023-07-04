package org.cardano.foundation.voting.service.leader_board;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.expire.ExpirationService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import static org.zalando.problem.Status.BAD_REQUEST;

@Service
@Slf4j
public class DefaultLeaderBoardService implements LeaderBoardService {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private ExpirationService expirationService;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private VoteRepository voteRepository;

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

        var votes = voteRepository.countAllByEventId(event);

        return Either.right(Leaderboard.ByEvent.builder()
                .event(event)
                //.votes(votes)
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

        if (!expirationService.isEventFinished(e) && !e.isCategoryResultsWhileVoting()) {
            return Either.left(Problem.builder()
                    .withTitle("VOTING_RESULTS_NOT_AVAILABLE")
                    .withDetail("Voting results not yet available, category:" + category)
                    .withStatus(BAD_REQUEST)
                    .build()
            );
        }

        // TODO: implement this
        return Either.right(Leaderboard.ByCategory.builder()
                .build()
        );
    }

}
