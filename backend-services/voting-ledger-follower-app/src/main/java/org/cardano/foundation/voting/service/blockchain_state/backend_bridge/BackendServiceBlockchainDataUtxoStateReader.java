package org.cardano.foundation.voting.service.blockchain_state.backend_bridge;

import com.bloxbean.cardano.client.backend.api.BackendService;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.Utxo;
import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataUtxoStateReader;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
public class BackendServiceBlockchainDataUtxoStateReader implements BlockchainDataUtxoStateReader {

    private final BackendService backendService;

    // TODO add utxo validation by checking witness against verification keys

    @Override
    public Either<Problem, List<Utxo>> getUTxOs(String address, List<String> verificationKeys) {
        try {
            var response = backendService.getUtxoService().getUtxos(address, 10, 1);

            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    return Either.right(List.of());
                }

                return Either.left(Problem.builder()
                        .withTitle("UTXO_ERROR")
                        .withDetail(format("Unable to get UTXOs from backend service, code:%d, msg:%s", response.code(), response.getResponse()))
                        .withStatus(INTERNAL_SERVER_ERROR)
                        .build()
                );
            }

            return Either.right(response.getValue().stream().map(utxo -> {
                return Utxo.builder()
                        .address(utxo.getAddress())
                        .txHash(utxo.getTxHash())
                        .txIndex(utxo.getOutputIndex())
                        .inlineDatum(Optional.ofNullable(utxo.getInlineDatum()))
                        .build();
            })
            .toList());

        } catch (Exception e) {
            return Either.left(Problem.builder()
                    .withTitle("UTXO_ERROR")
                    .withDetail("Unable to get UTXOs from backend service, reason:" + e.getMessage())
                    .withStatus(INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

}
