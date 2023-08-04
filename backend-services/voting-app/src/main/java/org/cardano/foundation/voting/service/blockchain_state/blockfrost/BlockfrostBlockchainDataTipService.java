package org.cardano.foundation.voting.service.blockchain_state.blockfrost;

import io.blockfrost.sdk.api.model.Block;
import io.vavr.control.Either;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

public class BlockfrostBlockchainDataTipService extends AbstractBlockfrostService implements BlockchainDataChainTipService {

    @Override
    public Either<Problem, ChainTip> getChainTip() {
        return latestBlock().map(block -> {
            return ChainTip.builder()
                    .epochNo(block.getEpoch())
                    .absoluteSlot(block.getSlot())
                    .hash(block.getHash())
                    .build();
        });
    }

    private Either<Problem, Block> latestBlock() {
        try {
            return Either.right(blockService.getLatestBlock());
        } catch (Exception e) {
            return Either.left(Problem.builder()
                    .withTitle("NO_DATA")
                    .withStatus(Status.NO_CONTENT)
                    .withDetail("Unable to get latest block from blockfrost API, reason:" + e.getMessage())
                    .build());
        }
    }

}
