package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/account")
@Slf4j
public class AccountResource {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/{network}/{event}/{stakeAddress}", method = GET, produces = "application/json")
    @Timed(value = "resource.account.find", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> findAccount(@PathVariable String network, @PathVariable String event, @PathVariable String stakeAddress) {
        return accountService.findAccount(network, event, stakeAddress)
                .fold(problem -> ResponseEntity.badRequest().body(problem),
                        response -> {
                            if (response.isEmpty()) {
                                return ResponseEntity.notFound().build();
                            }
                            return ResponseEntity.ok().body(response.orElseThrow());
                        });
    }

}
