package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/blockchain")
@Slf4j
public class BlockchainDataResource {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Autowired
    private BlockchainDataTransactionDetailsService blockchainDataTransactionDetailsService;

    @RequestMapping(value = "/tip", method = GET, produces = "application/json")
    @Timed(value = "resource.blockchain.tip", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> tip() {
        return blockchainDataChainTipService.getChainTip()
                .fold(problem -> ResponseEntity.status(Objects.requireNonNull(problem.getStatus()).getStatusCode()).body(problem),
                        chainTip -> ResponseEntity.ok().body(chainTip));
    }

    @RequestMapping(value = "/tx-details/{txHash}", method = GET, produces = "application/json")
    @Timed(value = "resource.tx-details", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> txDetails(@PathVariable String txHash) {
        return blockchainDataTransactionDetailsService.getTransactionDetails(txHash)
                .map(txDetails -> ResponseEntity.ok().body(txDetails))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
