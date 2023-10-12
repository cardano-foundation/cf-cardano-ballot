package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.coinselection.impl.LargestFirstUtxoSelectionStrategy;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.TxOutputBuilder;
import com.bloxbean.cardano.client.function.helper.BalanceTxBuilders;
import com.bloxbean.cardano.client.function.helper.InputBuilders;
import com.bloxbean.cardano.client.function.helper.MinAdaCheckers;
import com.bloxbean.cardano.client.plutus.api.PlutusObjectConverter;
import com.bloxbean.cardano.client.transaction.spec.TransactionOutput;
import com.bloxbean.cardano.client.transaction.spec.Value;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardanofoundation.hydra.cardano.client.lib.HydraOperatorSupplier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static java.math.BigDecimal.ZERO;

@Component
@RequiredArgsConstructor
@Slf4j
public class HydraVoteImporter {

    private final UtxoSupplier utxoSupplier;
    private final ProtocolParamsSupplier protocolParamsSupplier;
    private final TransactionSubmissionService transactionProcessor;
    private final HydraOperatorSupplier hydraOperatorSupplier;
    private final PlutusScriptLoader plutusScriptLoader;
    private final PlutusObjectConverter plutusObjectConverter;

    public Either<Problem, String> importVotes(List<Vote> votes) throws Exception {
        log.info("Importing number: {} votes", votes.size());

        val hydraOperator = hydraOperatorSupplier.getOperator();
        val sender = hydraOperator.getAddress();

        if (votes.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("No votes to import")
                    .withDetail("No votes to import")
                    .build());
        }

        val voteDatumList = votes.stream()
                .map(vote -> VoteDatum.builder()
                        .voterKey(vote.voterStakeAddress())
                        .category(vote.categoryId())
                        .proposal(vote.proposalId().toString())
                        .build()
                ).toList();

        // Create an empty output builder
        TxOutputBuilder txOutputBuilder = (context, outputs) -> {};

        for (val voteDatum : voteDatumList) {
            val categoryId = voteDatum.getCategory();
            val contract = plutusScriptLoader.getContract(categoryId);
            val contractAddress = plutusScriptLoader.getContractAddress(contract);

            val datum = plutusObjectConverter.toPlutusData(voteDatum);
            txOutputBuilder = txOutputBuilder.and((context, outputs) -> {
                val transactionOutput = TransactionOutput.builder()
                        .address(contractAddress)
                        .value(Value
                                .builder()
                                .coin(adaToLovelace(ZERO))
                                .build()
                        )
                        .inlineDatum(datum)
                        .build();

                val additionalLoveLace = MinAdaCheckers.minAdaChecker()
                        .apply(context, transactionOutput);

                val value = transactionOutput.getValue()
                        .plus(new Value(additionalLoveLace, null));
                transactionOutput.setValue(value);

                outputs.add(transactionOutput);
            });
        }

        val txBuilder = txOutputBuilder
                .buildInputs(InputBuilders.createFromSender(sender, sender))
                .andThen(BalanceTxBuilders.balanceTx(sender));

        val txBuilderContext = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier);
        txBuilderContext.setUtxoSelectionStrategy(new LargestFirstUtxoSelectionStrategy(utxoSupplier));
        val transaction = txBuilderContext
                .buildAndSign(txBuilder, hydraOperator.getTxSigner());

        val result = transactionProcessor.submitTransaction(transaction.serialize());
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                            .withTitle("Transaction submission failed")
                            .withDetail("Failure reason:" + result.getResponse())
                    .build());
        }

        return Either.right(result.getValue());
    }

}
