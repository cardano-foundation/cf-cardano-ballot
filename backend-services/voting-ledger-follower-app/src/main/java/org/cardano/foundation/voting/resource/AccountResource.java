package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.account.AccountService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.concurrent.TimeUnit.HOURS;
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
        var cacheControl = CacheControl.maxAge(1, HOURS)
                .noTransform()
                .mustRevalidate();

        return accountService.findAccount(eventId, stakeAddress)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .cacheControl(cacheControl)
                                    .body(problem);
                        },
                        account -> {
                            return ResponseEntity
                                    .ok()
                                    .cacheControl(cacheControl)
                                    .body(account);
                        });
    }

}
