package org.cardano.foundation.voting.resource;

import com.bloxbean.cardano.client.common.ADAConversionUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Leaderboard;
import org.cardano.foundation.voting.service.leader_board.DefaultLeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
public class LeaderboardResource {

    @Autowired
    private DefaultLeaderBoardService leaderBoardService;

    @RequestMapping(value = "/{event}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getEventLeaderBoard(@PathVariable String event) {
        return leaderBoardService.getEventLeaderboard(event)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/mock/{event}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getEventLeaderBoardMock(@PathVariable String event) {
        Random r = new Random();

        Leaderboard.ByEvent leaderboard = Leaderboard.ByEvent.builder()
                .event(event)
                .totalVotesCount(Math.abs(r.nextInt()))
                .totalVotingPower(String.valueOf(Math.abs(r.nextLong())))
                .build();

        return ResponseEntity.ok().body(leaderboard);
    }

    @RequestMapping(value = "/{event}/{category}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getCategoryLeaderBoard(@PathVariable String event, @PathVariable String category) {
        return leaderBoardService.getCategoryLeaderboard(event, category)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        response -> ResponseEntity.ok().body(response)
                );
    }

    @RequestMapping(value = "/mock/{event}/{category}", method = GET, produces = "application/json")
    @Timed(value = "resource.leaderboard.category", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getCategoryLeaderBoardMock(@PathVariable String event, @PathVariable String category) {
        var r = new Random();

        var leaderboard = Leaderboard.ByCategory.builder()
                .category(category)
                .proposals(
                        Map.of(
                                "YES", new Leaderboard.Votes(Math.abs(r.nextInt()), String.valueOf(ADAConversionUtil.adaToLovelace(Math.abs(r.nextInt())))),
                                "NO", new Leaderboard.Votes(Math.abs(r.nextInt()), String.valueOf(ADAConversionUtil.adaToLovelace(Math.abs(r.nextInt())))),
                                "ABSTAIN", new Leaderboard.Votes(r.nextInt(),  String.valueOf(ADAConversionUtil.adaToLovelace(Math.abs(r.nextInt()))))))
                .build();

        return ResponseEntity.ok().body(leaderboard);
    }

}
