package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.account.AccountService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/account")
@Slf4j
@RequiredArgsConstructor
public class AccountResource {

    private final AccountService accountService;

    private final CacheManager cacheManager;

    @RequestMapping(value = "/{eventId}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.account.find", percentiles = { 0.3, 0.5, 0.95 })
    @Cacheable("accounts")
    public ResponseEntity<?> findAccount(@PathVariable("eventId") String eventId, @PathVariable String stakeAddress) {
        return accountService.findAccount(eventId, stakeAddress)
                .fold(problem -> {
                            return ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem);
                        },
                        maybeAccount -> {
                            if (maybeAccount.isEmpty()) {
                                return ResponseEntity.notFound().build();
                            }

                            return ResponseEntity.ok().body(maybeAccount.orElseThrow());
                        });
    }

    @Scheduled(fixedRateString = "PT15M")
    public void clearCache() {
        log.info("account cache eviction...");

        cacheManager.getCache("accounts").clear();
    }

}
