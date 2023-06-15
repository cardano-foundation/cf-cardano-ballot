package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.leader_board.DefaultLeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
public class LeaderboardResource {

    @Autowired
    private DefaultLeaderBoardService leaderBoardService;

    @RequestMapping(value = "/results/{network}/{event}", method = POST, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getLeaderBoard(String network, String event) {
        return leaderBoardService.getLeaderboard(network, event)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        response -> ResponseEntity.ok().body(response)
                );
    }

}
