package org.cardano.foundation.voting.resource;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api/blockchain-data")
@Slf4j
public class BlockchainDataResource {

    @Autowired
    private BlockchainDataService blockchainDataService;

    @RequestMapping(value = "/tip", method = GET, produces = "application/json")
    @Timed(value = "resource.blockchain-data.get", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> blockchainData() {
        return ResponseEntity.ok(blockchainDataService.getChainTip());
    }

}
