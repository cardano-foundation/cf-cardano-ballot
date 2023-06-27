package org.cardano.foundation.voting.resource;

import com.bloxbean.cardano.client.util.HexUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.TxBody;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainTransactionSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/blockchain")
@Slf4j
public class BlockchainDataResource {

    @Autowired
    private BlockchainDataChainTipService blockchainDataChainTipService;

    @Autowired
    private BlockchainTransactionSubmissionService transactionSubmissionService;

    @RequestMapping(value = "/tip", method = GET, produces = "application/json")
    @Timed(value = "resource.blockchain.tip", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> tip() {
        return ResponseEntity.ok(blockchainDataChainTipService.getChainTip());
    }


    @RequestMapping(value = "/submit", method = POST, produces = "application/json")
    @Timed(value = "resource.blockchain.submit", percentiles = { 0.3, 0.5, 0.95 })
    public ResponseEntity<?> submit(@RequestBody TxBody txBody) {
        var tx = HexUtil.decodeHexString(txBody.txDataHex());

        return ResponseEntity.ok(Map.of("tx_hash", transactionSubmissionService.submitTransaction(tx)));
    }

}
