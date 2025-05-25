package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.WinnerLeaderboardSource;
import org.cardano.foundation.voting.service.leader_board.LeaderboardWinnersProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.cardano.foundation.voting.domain.WinnerLeaderboardSource.db;
import static org.cardano.foundation.voting.resource.Headers.X_Ballot_Force_LeaderBoard_Results;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.problem.Status.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/leaderboard/candidate")
@Slf4j
@Tag(name = "Leaderboard Candidate", description = "Operations related to the leaderboard for candidates")
public class LeaderboardCandidateResource {

    private final LeaderboardWinnersProvider leaderboardWinnersProvider;

    @Value("${leaderboard.force.results:false}")
    private boolean forceLeaderboardResultsAvailability;

    @RequestMapping(value = "/{eventId}/{categoryId}/results", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", histogram = true)
    public ResponseEntity<?> getCategoryLeaderBoardPerCategoryResults(@PathVariable("eventId") String eventId,
                                                               @PathVariable("categoryId") String categoryId,
                                                               @RequestHeader(value = X_Ballot_Force_LeaderBoard_Results, required = false, defaultValue = "false") boolean forceLeaderboardResults,
                                                               @Valid @RequestParam(name = "source") Optional<WinnerLeaderboardSource> winnerLeaderboardSourceM) {
        val winnerLeaderboardSource = winnerLeaderboardSourceM.orElse(db);

        val cacheControl = CacheControl.maxAge(5, MINUTES)
                .noTransform()
                .mustRevalidate();

        val forceLeaderboard = forceLeaderboardResults && forceLeaderboardResultsAvailability;

        val categoryLeaderboardE = leaderboardWinnersProvider
                .getWinnerLeaderboardSource(winnerLeaderboardSource)
                .getCategoryLeaderboardCandidate(eventId, categoryId, forceLeaderboard);

        return categoryLeaderboardE
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        proposalsInCategoryStatsM -> {
                            if (proposalsInCategoryStatsM.isEmpty()) {
                                val problem = Problem.builder()
                                        .withTitle("VOTING_RESULTS_NOT_YET_AVAILABLE")
                                        .withDetail("Leaderboard not yet available for event: " + eventId)
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity
                                        .status(problem.getStatus().getStatusCode())
                                        .body(problem);
                            }

                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(proposalsInCategoryStatsM.orElseThrow());
                        }
                );
    }

}
