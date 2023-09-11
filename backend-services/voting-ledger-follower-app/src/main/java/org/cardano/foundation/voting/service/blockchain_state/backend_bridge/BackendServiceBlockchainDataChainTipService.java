package org.cardano.foundation.voting.service.blockchain_state.backend_bridge;

import com.bloxbean.cardano.client.backend.api.BackendService;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ChainTip;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataChainTipService;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
public class BackendServiceBlockchainDataChainTipService implements BlockchainDataChainTipService {

    private final BackendService backendService;

    private final CardanoNetwork cardanoNetwork;

    @Override
    @Transactional(readOnly = true)
    public Either<Problem, ChainTip> getChainTip() {
        try {
            var latestBlock = backendService.getBlockService().getLatestBlock();

            if (latestBlock.isSuccessful()) {
                var block = latestBlock.getValue();

                return Either.right(ChainTip.builder()
                        .hash(block.getHash())
                        .epochNo(Optional.ofNullable(block.getEpoch()).orElse(-1))
                        .absoluteSlot(block.getSlot())
                        .network(cardanoNetwork)
                        .build());
            }

            return Either.left(Problem.builder()
                    .withTitle("CHAIN_TIP_NOT_FOUND")
                    .withDetail("Unable to get chain tip from backend service.")
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build()
            );

        } catch (Exception e) {
            return Either.left(Problem.builder()
                    .withTitle("CHAIN_TIP_NOT_FOUND")
                    .withDetail("Unable to get chain tip from backend service, reason:" + e.getMessage())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

}
