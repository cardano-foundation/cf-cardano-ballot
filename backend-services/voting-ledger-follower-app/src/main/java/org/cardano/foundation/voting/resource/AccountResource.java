package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.account.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/account")
@Slf4j
@RequiredArgsConstructor
public class AccountResource {

    private final AccountService accountService;

    @RequestMapping(value = "/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.account.find", histogram = true)
    public ResponseEntity<?> findAccount(@PathVariable("eventId") String eventId,
                                         @PathVariable String stakeAddress) {
        return accountService.findAccount(eventId, stakeAddress)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        maybeAccount -> {
                            if (maybeAccount.isEmpty()) {
                                return ResponseEntity.notFound().build();
                            }

                            var account = maybeAccount.orElseThrow();

                            return ResponseEntity.ok().body(account);
                        });
    }

}
