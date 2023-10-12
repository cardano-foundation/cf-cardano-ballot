package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.coinselection.impl.LargestFirstUtxoSelectionStrategy;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.helper.CollateralBuilders;
import com.bloxbean.cardano.client.function.helper.InputBuilders;
import com.bloxbean.cardano.client.function.helper.ScriptCallContextProviders;
import com.bloxbean.cardano.client.function.helper.model.ScriptCallContext;
import com.bloxbean.cardano.client.plutus.api.PlutusObjectConverter;
import com.bloxbean.cardano.client.plutus.spec.ExUnits;
import com.bloxbean.cardano.client.plutus.spec.PlutusV2Script;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.CreateVoteBatchRedeemer;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardano.foundation.voting.utils.BalanceUtil;
import org.cardanofoundation.hydra.cardano.client.lib.HydraOperatorSupplier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.plutus.spec.RedeemerTag.Spend;
import static java.util.Collections.emptySet;

@Component
@RequiredArgsConstructor
@Slf4j
public class HydraVoteBatcher {

    private final UtxoSupplier utxoSupplier;
    private final ProtocolParamsSupplier protocolParamsSupplier;
    private final TransactionProcessor transactionProcessor;
    private final HydraOperatorSupplier hydraOperatorSupplier;
    private final PlutusScriptLoader plutusScriptLoader;
    private final VoteUtxoFinder voteUtxoFinder;
    private final PlutusObjectConverter plutusObjectConverter;

    public Either<Problem, Optional<String>> createAndPostBatchTransaction(
            String contractCategoryId,
            int batchSize)
            throws CborSerializationException, ApiException {

        val utxoTuples = voteUtxoFinder.getUtxosWithVotes(contractCategoryId, batchSize);

        log.info("Found votes[UTxOs]: {}", utxoTuples.size());

        if (utxoTuples.isEmpty()) {
            log.warn("No utxo found");

            return Either.right(Optional.empty());
        }

        val hydraOperator = hydraOperatorSupplier.getOperator();
        val operatorAddress = hydraOperator.getAddress();
        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        log.info("Sender Address: " + operatorAddress);
        log.info("Script Address: " + contractAddress);

        val categoryResultsDatum = CategoryResultsDatum.empty(contractCategoryId);
        for (val tuple : utxoTuples) {
            val voteDatum = tuple._2;
            val categoryId = new String(voteDatum.getCategory());

            if (contractCategoryId.equals(categoryId)) {
                val proposal = new String(voteDatum.getProposal());
                val accumulator = categoryResultsDatum.getOr(proposal, 0);

                categoryResultsDatum.add(categoryId, accumulator + 1);
            }
        }

        val utxoSelectionStrategy = new LargestFirstUtxoSelectionStrategy(utxoSupplier);
        val outputAmount = new Amount(LOVELACE, adaToLovelace(1));
        val collateralUtxos = utxoSelectionStrategy.select(operatorAddress, outputAmount, emptySet());

        // Build the expected output
        val outputDatum = plutusObjectConverter.toPlutusData(categoryResultsDatum);
        val output = Output.builder()
                .address(contractAddress)
                .datum(outputDatum)
                .inlineDatum(true)
                .assetName(LOVELACE)
                .qty(adaToLovelace(1))
                .build();

        val scriptUtxos = utxoTuples.stream()
                .map(utxoVoteDatumTuple -> utxoVoteDatumTuple._1)
                .toList();

        val extraInputs = utxoSelectionStrategy.select(operatorAddress, new Amount(LOVELACE, adaToLovelace(2)), Set.of());

        List<Utxo> allInputs = new ArrayList<>();
        allInputs.addAll(scriptUtxos);
        allInputs.addAll(extraInputs);

        var txBuilder = output.outputBuilder()
                .buildInputs(InputBuilders.createFromUtxos(allInputs, operatorAddress))
                .andThen(CollateralBuilders.collateralOutputs(operatorAddress, new ArrayList<>(collateralUtxos))); // CIP-40

        val scriptCallContexts = scriptUtxos.stream().map(utxo -> ScriptCallContext
                        .builder()
                        .script(contract)
                        .utxo(utxo)
                        .exUnits(ExUnits.builder()  // Exact exUnits will be calculated later
                                .mem(BigInteger.valueOf(0))
                                .steps(BigInteger.valueOf(0))
                                .build()
                        )
                        .redeemer(plutusObjectConverter.toPlutusData(CreateVoteBatchRedeemer.create()))
                        .redeemerTag(Spend).build())
                .toList();

        for (var scriptCallContext : scriptCallContexts) {
            txBuilder = txBuilder.andThen(ScriptCallContextProviders.createFromScriptCallContext(scriptCallContext));
        }

        txBuilder = txBuilder.andThen((context, txn) -> {
            val protocolParams = protocolParamsSupplier.getProtocolParams();
            val utxos = context.getUtxos();
            log.info("Votes utxos size: {}", utxos.size());

            val evalReedemers = PlutusScriptLoader.evaluateExUnits(txn, utxos, protocolParams);

            val redeemers = txn.getWitnessSet().getRedeemers();
            for (val redeemer : redeemers) {
                evalReedemers.stream().filter(evalReedemer -> evalReedemer.getIndex().equals(redeemer.getIndex()))
                        .findFirst()
                        .ifPresent(evalRedeemer -> {
                            redeemer.setExUnits(evalRedeemer.getExUnits());
                        });
            }

            // Remove all scripts from witness and just add one
            txn.getWitnessSet().getPlutusV2Scripts().clear();
            txn.getWitnessSet().getPlutusV2Scripts().add((PlutusV2Script) contract);
        })
        .andThen(BalanceUtil.balanceTx(operatorAddress, 1));

        val txBuilderContext = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier);
        val transaction = txBuilderContext.buildAndSign(txBuilder, hydraOperator.getTxSigner());

        log.info("Fee:{} lovelaces", transaction.getBody().getFee());

        val result = transactionProcessor.submitTransaction(transaction.serialize());
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                    .withTitle("Transaction failed")
                    .withDetail("Transaction failed. " + result.getResponse())
                    .build());
        }

        return Either.right(Optional.of(result.getValue()));
    }

}
