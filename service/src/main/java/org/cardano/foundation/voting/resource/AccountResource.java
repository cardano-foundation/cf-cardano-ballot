package org.cardano.foundation.voting.resource;

import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@Slf4j
public class AccountResource {

    @Autowired
    private AccountService accountService;

    public ResponseEntity<?> getAccount(String address) {
        var maybeAccount = accountService.findAccount(address);

        if (maybeAccount.isEmpty()) {
            // TODO problem from zalando
            // https://github.com/zalando/problem
            return ResponseEntity.notFound().build();
        }

        var account = maybeAccount.orElseThrow();

        return ResponseEntity.ok(account);
    }

}
