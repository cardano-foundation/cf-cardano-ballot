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
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.TransactionOutput;
import com.bloxbean.cardano.client.transaction.spec.Value;
import com.bloxbean.cardano.client.util.JsonUtil;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardanofoundation.hydra.cardano.client.lib.CardanoOperatorSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static java.math.BigDecimal.ZERO;
import static org.cardano.foundation.voting.utils.MoreFees.changeTransactionCost;

@Component
@Slf4j
public class HydraVoteImporter {

    @Autowired
    private UtxoSupplier utxoSupplier;

    @Autowired
    private ProtocolParamsSupplier protocolParamsSupplier;

    @Autowired
    @Qualifier("hydra-transaction-submission-service")
    private TransactionSubmissionService transactionProcessor;

    @Autowired
    //@Qualifier("hydra-operator-supplier")
    private CardanoOperatorSupplier cardanoOperatorSupplier;

    @Autowired
    private PlutusScriptLoader plutusScriptLoader;

    @Autowired
    private PlutusObjectConverter plutusObjectConverter;

    public Either<Problem, String> importVotes(String contractEventId,
                                               List<Vote> votes) throws Exception {
        log.info("Importing number: {} votes", votes.size());

        val operator = cardanoOperatorSupplier.getOperator();
        val sender = operator.getAddress();

        if (votes.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("No votes to import")
                    .withDetail("No votes to import")
                    .build());
        }

        val voteDatumList = votes.stream()
                .map(vote -> VoteDatum.builder()
                        .voteId(vote.voteId().toString())
                        .voterKey(vote.voterStakeAddress())
                        .categoryId(vote.categoryId())
                        .proposalId(vote.proposalId().toString())
                        .build()
                ).toList();

        // Create an empty output builder
        TxOutputBuilder txOutputBuilder = (context, outputs) -> {};

        for (val voteDatum : voteDatumList) {
            val categoryId = voteDatum.getCategoryId();
            val contract = plutusScriptLoader.getContract(contractEventId, categoryId);
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

         val transaction = txBuilderContext.build(txBuilder);

        changeTransactionCost(transaction);

        val signedTx = operator.getTxSigner().sign(transaction);

        //System.out.println(JsonUtil.getPrettyJson(signedTx));

        val result = transactionProcessor.submitTransaction(signedTx);
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                            .withTitle("Transaction submission failed")
                            .withDetail("Failure reason:" + result.getResponse())
                    .build());
        }

        return Either.right(result.getValue());
    }

}
