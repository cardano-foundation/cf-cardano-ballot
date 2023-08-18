//package org.cardano.foundation.voting.service.blockchain_state.yaci;
//
//import com.bloxbean.cardano.yaci.store.blocks.domain.Block;
//import com.bloxbean.cardano.yaci.store.blocks.service.BlockService;
//import com.bloxbean.cardano.yaci.store.transaction.service.TransactionService;
//import lombok.extern.slf4j.Slf4j;
//import org.cardano.foundation.voting.domain.TransactionDetails;
//import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataTransactionDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Optional;
//
//@Slf4j
//public class YaciTransactionDetailsBlockchainDataService implements BlockchainDataTransactionDetailsService {
//
//    @Autowired
//    private BlockService blockService;
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @Override
//    public Optional<TransactionDetails> getTransactionDetails(String transactionHash) {
//        return transactionService.getTransaction(transactionHash)
//                .flatMap(txn -> {
//                    return blockService.getBlockByNumber(txn.getBlockHeight()).map(block -> {
//                        var blockchainTipSlot = blockService.getLatestBlock()
//                                .map(Block::getSlot)
//                                .orElse(block.getSlot()); // we fallback to block's hash slot if we can't find the blockchain tip
//
//                        var blockConfirmations = blockchainTipSlot - block.getSlot();
//
//                        return TransactionDetails.builder()
//                                .transactionsConfirmations(blockConfirmations)
//                                .finalityScore(TransactionDetails.FinalityScore.fromConfirmations(blockConfirmations))
//                                .blockHash(block.getHash())
//                                .absoluteSlot(txn.getSlot())
//                                .build();
//                    });
//                });
//    }
//
//}
