//package org.cardano.foundation.voting.service.blockchain_state.yaci;
//
//import com.bloxbean.cardano.yaci.store.blocks.service.BlockService;
//import io.vavr.control.Either;
//import org.cardano.foundation.voting.domain.ChainTip;
//import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.zalando.problem.Problem;
//
//import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
//
//public class YaciStoreBlockchainDataChainTipService implements BlockchainDataChainTipService {
//
//    @Autowired
//    private BlockService blockService;
//
//    @Override
//    public Either<Problem, ChainTip> getChainTip() {
//        return blockService.getLatestBlock()
//                .map(block -> ChainTip.builder()
//                        .hash(block.getHash())
//                        .epochNo(block.getEpochNumber())
//                        .absoluteSlot(block.getSlot())
//                        .build())
//                .<Either<Problem, ChainTip>>map(Either::right)
//                .orElseGet(() -> Either.left(Problem.builder()
//                        .withTitle("CHAIN_TIP_NOT_FOUND")
//                        .withDetail("Unable to get chain tip from yaci")
//                        .withStatus(INTERNAL_SERVER_ERROR)
//                        .build()));
//    }
//
//}
