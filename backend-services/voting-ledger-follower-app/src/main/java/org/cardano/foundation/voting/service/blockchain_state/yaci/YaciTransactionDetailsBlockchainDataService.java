package org.cardano.foundation.voting.service.blockchain_state.yaci;

import com.bloxbean.cardano.yaci.store.api.blocks.service.BlockService;
import com.bloxbean.cardano.yaci.store.api.transaction.service.TransactionService;
import com.bloxbean.cardano.yaci.store.blocks.domain.Block;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.TransactionDetails;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YaciTransactionDetailsBlockchainDataService implements BlockchainDataTransactionDetailsService {

    private final BlockService blockService;

    private final TransactionService transactionService;

    private final CardanoNetwork cardanoNetwork;

    private final CacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("trxDetailsCache")
    public Either<Problem, Optional<TransactionDetails>> getTransactionDetails(String transactionHash) {
        return Either.right(transactionService.getTransaction(transactionHash)
                .flatMap(txn -> {
                    return blockService.getBlockByNumber(txn.getBlockHeight()).map(block -> {
                        var blockchainTipSlot = blockService.getLatestBlock()
                                .map(Block::getSlot)
                                .orElse(block.getSlot()); // we fallback to block's hash slot if we can't find the blockchain tip

                        var blockConfirmations = blockchainTipSlot - block.getSlot();

                        return TransactionDetails.builder()
                                .transactionsConfirmations(blockConfirmations)
                                .transactionHash(txn.getHash())
                                .finalityScore(TransactionDetails.FinalityScore.fromConfirmations(blockConfirmations))
                                .blockHash(block.getHash())
                                .absoluteSlot(txn.getSlot())
                                .network(cardanoNetwork)
                                .build();
                    });
                }));
    }

    @Scheduled(fixedRateString = "PT15S")
    public void evictTxDetailsCache() {
        cacheManager.getCache("trxDetailsCache")
                .clear();
    }

}
