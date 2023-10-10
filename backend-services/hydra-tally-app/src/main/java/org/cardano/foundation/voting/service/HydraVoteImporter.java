package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.coinselection.impl.LargestFirstUtxoSelectionStrategy;
import com.bloxbean.cardano.client.function.TxBuilder;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.TxOutputBuilder;
import com.bloxbean.cardano.client.function.helper.BalanceTxBuilders;
import com.bloxbean.cardano.client.function.helper.InputBuilders;
import com.bloxbean.cardano.client.function.helper.MinAdaCheckers;
import com.bloxbean.cardano.client.plutus.api.PlutusObjectConverter;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.transaction.spec.TransactionOutput;
import com.bloxbean.cardano.client.transaction.spec.Value;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardano.foundation.voting.utils.MoreHash;
import org.cardanofoundation.hydra.cardano.client.lib.HydraOperatorSupplier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static java.math.BigDecimal.ZERO;
import static org.cardano.foundation.voting.utils.MoreHash.unsignedHash;
import static org.cardano.foundation.voting.utils.MoreUUID.uuidHash;

@Component
@RequiredArgsConstructor
@Slf4j
public class HydraVoteImporter {

    private final UtxoSupplier utxoSupplier;
    private final ProtocolParamsSupplier protocolParamsSupplier;
    private final TransactionProcessor transactionProcessor;
    private final HydraOperatorSupplier hydraOperatorSupplier;
    private final PlutusScripts plutusScripts;

    private final PlutusObjectConverter plutusObjectConverter;

    public Either<Problem, String> importVotes(List<Vote> votes) throws Exception {
        log.info("Importing number: {} votes", votes.size());

        if (votes.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("No votes to import")
                    .withDetail("No votes to import")
                    .build());
        }

        val voteDatumList = votes.stream()
                .map(vote -> VoteDatum.builder()
                        .voterKey(vote.voterStakeAddress()) // TODO ???
                        .votingPower(1) // TODO hard-coded for USER-BASED events for now
                        .category(unsignedHash(vote.categoryId()))
                        .proposal(unsignedHash(vote.proposalId()))
                        .build()
                ).toList();

        String sender = hydraOperatorSupplier.getOperator().getAddress();
        log.debug("Sender Address: " + sender);
        String voteBatchContractAddress = plutusScripts.getContractAddress();
        log.debug("Contract Address: " + voteBatchContractAddress);

        // Create an empty output builder
        TxOutputBuilder txOutputBuilder = (context, outputs) -> {};

        // Iterate through voteDatumLists and create TransactionOutputs
        for (var voteDatum : voteDatumList) {
            PlutusData datum = plutusObjectConverter.toPlutusData(voteDatum);
            txOutputBuilder = txOutputBuilder.and((context, outputs) -> {
                TransactionOutput transactionOutput = TransactionOutput.builder()
                        .address(voteBatchContractAddress)
                        .value(Value
                                .builder()
                                .coin(adaToLovelace(ZERO))
                                .build()
                        )
                        .inlineDatum(datum)
                        .build();

                BigInteger additionalLoveLace = MinAdaCheckers.minAdaChecker().apply(context, transactionOutput);
                transactionOutput.setValue(transactionOutput.getValue().plus(new Value(additionalLoveLace, null)));

                outputs.add(transactionOutput);
            });
        }

        TxBuilder txBuilder = txOutputBuilder
                .buildInputs(InputBuilders.createFromSender(sender, sender))
                .andThen(BalanceTxBuilders.balanceTx(sender));

        TxBuilderContext txBuilderContext = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier);
        txBuilderContext.setUtxoSelectionStrategy(new LargestFirstUtxoSelectionStrategy(utxoSupplier));
        Transaction transaction = txBuilderContext
                .buildAndSign(txBuilder, hydraOperatorSupplier.getOperator().getTxSigner());

        Result<String> result = transactionProcessor.submitTransaction(transaction.serialize());
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                            .withTitle("Transaction submission failed")
                            .withDetail("Failure reason:" + result.getResponse())
                    .build());
        }

        return Either.right(result.getValue());
    }

}
