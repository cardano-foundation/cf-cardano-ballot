package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.leader_board.DefaultLeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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

}
