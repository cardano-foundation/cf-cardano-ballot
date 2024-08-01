package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.WalletType;
import org.cardano.foundation.voting.service.account.AccountService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/account")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Account", description = "The account API")
public class AccountResource {

    private final AccountService accountService;

    @RequestMapping(value = "/{eventId}/{walletType}/{walletId}", method = GET, produces = "application/json")
    @Timed(value = "resource.account.find", histogram = true)
    @Operation(summary = "Find account details associated with an event, wallet type and wallet id",
            description = "This endpoint fetches the account details based on a given event ID, wallet type and wallet id. " +
                    "If the account is not found, an appropriate error is returned.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved the Account object",
                        content = { @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Account.class)) }),
                @ApiResponse(responseCode = "400", description = "Bad request due to validation issues or malformed request",
                        content = { @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Problem.class)) }),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<?> findAccount(
            @Parameter(description = "Event ID associated with the account", required = true)
            @PathVariable("eventId") String eventId,

            @Parameter(description = "Wallet type of a given account", required = true)
            @PathVariable("walletType") @Valid WalletType walletType,

            @Parameter(description = "WalletId of the account", required = true)
            @PathVariable String walletId) {
        var cacheControl = CacheControl.maxAge(1, HOURS)
                .noTransform()
                .mustRevalidate();

        return accountService.findAccount(eventId, walletType, walletId)
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
