package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/leaderboard")
@Slf4j
public class LeaderboardResource {

    @Autowired
    private LeaderBoardService leaderBoardService;

    @RequestMapping(value = "/results", method = POST, produces = "application/json")
    @Timed(value = "resource.leaderboard.event", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> getLeaderBoard() {
        return ResponseEntity.ok().build();
    }

}
