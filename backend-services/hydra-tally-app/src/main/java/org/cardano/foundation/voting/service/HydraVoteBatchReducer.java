package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.coinselection.impl.LargestFirstUtxoSelectionStrategy;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.Output;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.helper.CollateralBuilders;
import com.bloxbean.cardano.client.function.helper.InputBuilders;
import com.bloxbean.cardano.client.function.helper.ScriptCallContextProviders;
import com.bloxbean.cardano.client.function.helper.model.ScriptCallContext;
import com.bloxbean.cardano.client.plutus.spec.ExUnits;
import com.bloxbean.cardano.client.plutus.spec.PlutusV2Script;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.ReduceVoteBatchRedeemer;
import org.cardano.foundation.voting.domain.UTxOCategoryResult;
import org.cardano.foundation.voting.domain.converter.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.converter.ReduceVoteBatchRedeemerConverter;
import org.cardanofoundation.hydra.cardano.client.lib.submit.TransactionSubmissionService;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.WalletSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static com.bloxbean.cardano.client.common.CardanoConstants.LOVELACE;
import static com.bloxbean.cardano.client.crypto.KeyGenUtil.getKeyHash;
import static com.bloxbean.cardano.client.plutus.spec.RedeemerTag.Spend;
import static java.util.Collections.emptySet;
import static org.cardano.foundation.voting.service.PlutusScriptLoader.evaluateExUnits;
import static org.cardano.foundation.voting.utils.BalanceUtil.balanceTx;
import static org.cardanofoundation.hydra.core.utils.HexUtils.decodeHexString;

@Component
@Slf4j
public class HydraVoteBatchReducer {

    @Autowired
    private VoteUtxoFinder voteUtxoFinder;

    @Autowired
    private UtxoSupplier utxoSupplier;

    @Autowired
    private ProtocolParamsSupplier protocolParamsSupplier;

    @Autowired
    @Qualifier("hydra-transaction-submission-service")
    private TransactionSubmissionService transactionProcessor;

    @Autowired
    private WalletSupplier walletSupplier;

    @Autowired
    private PlutusScriptLoader plutusScriptLoader;

    @Autowired
    private Network network;

    @Autowired
    private CategoryResultsDatumConverter categoryResultsDatumConverter;

    @Autowired
    private ReduceVoteBatchRedeemerConverter reduceVoteBatchRedeemerConverter;

    public void batchVotesPerCategory(String contractEventId,
                                      String contractOrganiser,
                                      String contractCategoryId,
                                      int batchSize) throws CborSerializationException {
        val contract = plutusScriptLoader.getContract(contractCategoryId);

        Either<Problem, Optional<String>> transactionResultE;
        do {
            transactionResultE = postReduceBatchTransaction(contractEventId,
                    contractOrganiser,
                    contractCategoryId,
                    batchSize
            );

            if (transactionResultE.isEmpty()) {
                log.error("Reducing votes failed, reason:{}", transactionResultE.getLeft());
                return;
            }

            val resultM = transactionResultE.get();

            if (resultM.isEmpty()) {
                //log.info("No more reducers to create within category: {}", contractCategoryId);
                break;
            }

            val txId = resultM.orElseThrow();

            log.info("TxId: " + txId);

        } while (transactionResultE.isRight() && transactionResultE.get().isPresent());
    }

    private Either<Problem, Optional<String>> postReduceBatchTransaction(String contractEventId,
                                                                         String contractOrganiser,
                                                                         String contractCategoryId,
                                                                         int batchSize) throws CborSerializationException {
        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        val wallet = walletSupplier.getWallet();
        val sender = wallet.getBech32Address(network);
        val senderVerificationKeyBlake224 = getKeyHash(wallet.getVerificationKey());

        val utxosWithCategoryResults = voteUtxoFinder.getUtxosWithCategoryResults(contractEventId,
                contractOrganiser,
                contractCategoryId,
                batchSize
        );

        if (utxosWithCategoryResults.isEmpty()) {
            //log.warn("No utxo found");

            return Either.right(Optional.empty());
        }

        if (utxosWithCategoryResults.size() == 1) {
            //log.info("Only final reduction left!");

            return Either.right(Optional.empty());
        }

        val categoryResultsDatums = utxosWithCategoryResults.stream()
                .map(UTxOCategoryResult::categoryResultsDatum)
                .toList();

        val reduceVoteBatchDatum = categoryResultsDatum(contractEventId,
                contractOrganiser,
                contractCategoryId,
                categoryResultsDatums
        );

        //System.out.println("Reduction:" + JsonUtil.getPrettyJson(reduceVoteBatchDatum));

        val utxoSelectionStrategy = new LargestFirstUtxoSelectionStrategy(utxoSupplier);
        val collateralUtxos = utxoSelectionStrategy.select(sender, new Amount(LOVELACE, adaToLovelace(2)), emptySet());

        val outputDatum = categoryResultsDatumConverter.toPlutusData(reduceVoteBatchDatum);

        val output1 = Output.builder()
                .address(contractAddress)
                .datum(outputDatum)
                .inlineDatum(true)
                .assetName(LOVELACE)
                .qty(adaToLovelace(1))
                .build();

        val scriptUtxos = utxosWithCategoryResults
                .stream()
                .map(UTxOCategoryResult::utxo)
                .toList();

        val extraInputs = utxoSelectionStrategy.select(sender, new Amount(LOVELACE, adaToLovelace(2)), Set.of());

        List<Utxo> allInputs = new ArrayList<>();
        allInputs.addAll(scriptUtxos);
        allInputs.addAll(extraInputs);

        val scriptCallContexts = scriptUtxos.stream().map(utxo -> ScriptCallContext
                        .builder()
                        .script(contract)
                        .utxo(utxo)
                        .exUnits(ExUnits.builder()  // Exact exUnits will be calculated later
                                .mem(BigInteger.valueOf(0))
                                .steps(BigInteger.valueOf(0))
                                .build())
                        .redeemer(reduceVoteBatchRedeemerConverter.toPlutusData(ReduceVoteBatchRedeemer.create()))
                        .redeemerTag(Spend).build())
                .toList();

        var txBuilder = output1.outputBuilder()
                .buildInputs(InputBuilders.createFromUtxos(allInputs, sender))
                //.andThen(output2.outputBuilder().buildInputs(InputBuilders.createFromSender(sender, sender)))
                .andThen(CollateralBuilders.collateralOutputs(sender, new ArrayList<>(collateralUtxos))); // CIP-40

        for (val scriptCallContext : scriptCallContexts) {
            txBuilder = txBuilder.andThen(ScriptCallContextProviders.createFromScriptCallContext(scriptCallContext));
        }

        txBuilder = txBuilder.andThen((context, txn) -> {
                    val protocolParams = protocolParamsSupplier.getProtocolParams();
                    val utxos = context.getUtxos();

                    txn.getBody().getRequiredSigners().add(decodeHexString(senderVerificationKeyBlake224));

                    val evaluatedExUnits = evaluateExUnits(txn, utxos, protocolParams);

                    val redeemers = txn.getWitnessSet().getRedeemers();
                    for (val redeemer : redeemers) { //Update costs
                        evaluatedExUnits.stream().filter(evalReedemer -> evalReedemer.getIndex().equals(redeemer.getIndex()))
                                .findFirst()
                                .ifPresent(evalRedeemer -> redeemer.setExUnits(evalRedeemer.getExUnits()));
                    }

                    // Remove all scripts from witness and just add one
                    txn.getWitnessSet().getPlutusV2Scripts().clear();
                    txn.getWitnessSet().getPlutusV2Scripts().add((PlutusV2Script) contract);
                })
                .andThen(balanceTx(sender, 1));

        val txBuilderContext = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier);
        val transaction = txBuilderContext.build(txBuilder);

        val result = transactionProcessor.submitTransaction(wallet.getTxSigner().sign(transaction));
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                    .withTitle("Transaction failed")
                    .withDetail("Transaction failed. " + result.getResponse())
                    .build());
        }

        return Either.right(Optional.of(result.getValue()));
    }

    public static CategoryResultsDatum categoryResultsDatum(String eventId,
                                                            String contractOrganiser,
                                                            String contractCategoryId,
                                                            List<CategoryResultsDatum> categoryResultsDataList) {
        val groupResultBatchDatum = CategoryResultsDatum.empty(eventId, contractOrganiser, contractCategoryId);

        for (val categoryResultsDatum : categoryResultsDataList) {
            categoryResultsDatum.getResults()
                    .forEach(groupResultBatchDatum::add);
        }

        return groupResultBatchDatum;
    }

}
