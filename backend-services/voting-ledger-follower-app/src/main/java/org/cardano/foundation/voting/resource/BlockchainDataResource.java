package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.zalando.problem.Status.NOT_FOUND;

@RestController
@RequestMapping("/api/blockchain")
@Slf4j
@RequiredArgsConstructor
public class BlockchainDataResource {

    private final BlockchainDataChainTipService blockchainDataChainTipService;

    private final BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @RequestMapping(value = "/tip", method = GET, produces = "application/json")
    @Timed(value = "resource.blockchain.tip", histogram = true)
    public ResponseEntity<?> tip() {
        return blockchainDataChainTipService.getChainTip()
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        chainTip -> {
                            return ResponseEntity
                                    .ok()
                                    .body(chainTip);
                        });
    }

    @RequestMapping(value = "/tx-details/{txHash}", method = GET, produces = "application/json")
    @Timed(value = "resource.tx-details", histogram = true)
    public ResponseEntity<?> txDetails(@PathVariable("txHash") String txHash) {
        return blockchainDataTransactionDetailsService.getTransactionDetails(txHash)
                .fold(problem -> {
                            return ResponseEntity
                                    .status(problem.getStatus().getStatusCode())
                                    .body(problem);
                        },
                        maybeTxDetails -> {
                            if (maybeTxDetails.isEmpty()) {
                                var problem = Problem.builder()
                                        .withTitle("TX_NOT_FOUND")
                                        .withDetail("Transaction with hash: " + txHash + " not found!")
                                        .withStatus(NOT_FOUND)
                                        .build();

                                return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
                            }

                            var txDetails = maybeTxDetails.orElseThrow();

                            return ResponseEntity.ok().body(txDetails);
                        });
    }

}
