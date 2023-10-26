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
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.CreateVoteBatchRedeemer;
import org.cardano.foundation.voting.domain.UTxOVote;
import org.cardano.foundation.voting.utils.BalanceUtil;
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
import static org.cardano.foundation.voting.utils.MoreFees.changeTransactionCost;
import static org.cardanofoundation.hydra.core.utils.HexUtils.decodeHexString;

@Component
@Slf4j
public class HydraVoteBatcher {

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
    private CategoryResultsDatumConverter categoryResultsDatumConverter;

    @Autowired
    private Network network;

    @Autowired
    private org.cardano.foundation.voting.domain.CreateVoteBatchRedeemerConverter createVoteBatchRedeemerConverter;


    public void batchVotesPerCategory(String contractEventId,
                                      String contractOrganiser,
                                      String contractCategoryId,
                                      int batchSize) throws CborSerializationException {
        val contract = plutusScriptLoader.getContract(contractEventId, contractOrganiser, contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        //log.info("Contract Address: {}", contractAddress);

        Either<Problem, Optional<String>> transactionResultE;
        do {
            transactionResultE = createAndPostBatchTransaction(contractEventId, contractOrganiser, contractCategoryId, batchSize);

            if (transactionResultE.isEmpty()) {
                log.error("Batching votes failed, reason:{}", transactionResultE.getLeft());
                return;
            }

            val batchTransactionResultM = transactionResultE.get();

            if (batchTransactionResultM.isEmpty()) {
                //log.info("No more batches to create within category: {}", contractCategoryId);
                break;
            }

            val txId = batchTransactionResultM.orElseThrow();

            log.info("Batched votes, txId: " + txId);

        } while (transactionResultE.isRight() && transactionResultE.get().isPresent());
    }

    private Either<Problem, Optional<String>> createAndPostBatchTransaction(
            String contractEventId,
            String contractOrganiser,
            String contractCategoryId,
            int batchSize)
            throws CborSerializationException {

        val utxosWithVotes = voteUtxoFinder.getUtxosWithVotes(contractEventId, contractOrganiser, contractCategoryId, batchSize);

        //log.info("Found votes[UTxOs]: {}", utxosWithVotes.size());

        if (utxosWithVotes.isEmpty()) {
            //log.warn("No utxo found");

            return Either.right(Optional.empty());
        }

        val wallet = walletSupplier.getWallet();
        val sender = wallet.getAddress(network);
        val contract = plutusScriptLoader.getContract(contractEventId, contractOrganiser, contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);
        val senderVerificationKeyBlake224 = getKeyHash(wallet.getVerificationKey());

        val categoryResultsDatum = CategoryResultsDatum.empty(contractEventId, contractOrganiser, contractCategoryId);

        for (val uTxOVote : utxosWithVotes) {
            val voteDatum = uTxOVote.voteDatum();
            val categoryId = voteDatum.getCategoryId();

            if (contractCategoryId.equals(categoryId)) {
                val proposalId = voteDatum.getProposalId();

                categoryResultsDatum.add(proposalId, 1);
            }
        }

        //System.out.println("Batch:" + JsonUtil.getPrettyJson(categoryResultsDatum));

        val utxoSelectionStrategy = new LargestFirstUtxoSelectionStrategy(utxoSupplier);
        val outputAmount = new Amount(LOVELACE, adaToLovelace(1));
        val collateralUtxos = utxoSelectionStrategy.select(sender, outputAmount, emptySet());

        // Build the expected output
        val outputDatum = categoryResultsDatumConverter.toPlutusData(categoryResultsDatum);
        val output = Output.builder()
                .address(contractAddress)
                .datum(outputDatum)
                .inlineDatum(true)
                .assetName(LOVELACE)
                .qty(adaToLovelace(1))
                .build();

        val scriptUtxos = utxosWithVotes.stream()
                .map(UTxOVote::utxo)
                .toList();

        val extraInputs = utxoSelectionStrategy.select(sender, new Amount(LOVELACE, adaToLovelace(2)), Set.of());

        List<Utxo> allInputs = new ArrayList<>();
        allInputs.addAll(scriptUtxos);
        allInputs.addAll(extraInputs);

        var txBuilder = output.outputBuilder()
                .buildInputs(InputBuilders.createFromUtxos(allInputs, sender))
                .andThen(CollateralBuilders.collateralOutputs(sender, new ArrayList<>(collateralUtxos))); // CIP-40

        val scriptCallContexts = scriptUtxos.stream().map(utxo -> ScriptCallContext
                        .builder()
                        .script(contract)
                        .utxo(utxo)
                        .exUnits(ExUnits.builder()  // Exact exUnits will be calculated later
                                .mem(BigInteger.valueOf(0))
                                .steps(BigInteger.valueOf(0))
                                .build()
                        )
                        .redeemer(createVoteBatchRedeemerConverter.toPlutusData(CreateVoteBatchRedeemer.create()))
                        .redeemerTag(Spend).build())
                .toList();

        for (var scriptCallContext : scriptCallContexts) {
            txBuilder = txBuilder.andThen(ScriptCallContextProviders.createFromScriptCallContext(scriptCallContext));
        }

        txBuilder = txBuilder.andThen((context, txn) -> {
            val protocolParams = protocolParamsSupplier.getProtocolParams();
            val utxos = context.getUtxos();

            txn.getBody().getRequiredSigners().add(decodeHexString(senderVerificationKeyBlake224));

            val evalReedemers = PlutusScriptLoader.evaluateExUnits(txn, utxos, protocolParams);

            val redeemers = txn.getWitnessSet().getRedeemers();
            for (val redeemer : redeemers) {
                evalReedemers.stream()
                        .filter(evalReedemer -> evalReedemer.getIndex().equals(redeemer.getIndex()))
                        .findFirst()
                        .ifPresent(evalRedeemer -> redeemer.setExUnits(evalRedeemer.getExUnits()));
            }

            // Remove all scripts from witness and just add one
            txn.getWitnessSet().getPlutusV2Scripts().clear();
            txn.getWitnessSet().getPlutusV2Scripts().add((PlutusV2Script) contract);

        })
        .andThen(BalanceUtil.balanceTx(sender, 1));

        val txBuilderContext = TxBuilderContext.init(utxoSupplier, protocolParamsSupplier);
        val transaction = txBuilderContext.build(txBuilder);

        changeTransactionCost(transaction);

        val result = transactionProcessor.submitTransaction(wallet.getTxSigner().sign(transaction));

        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                    .withTitle("Transaction failed")
                    .withDetail("Transaction failed. " + result.getResponse())
                    .build());
        }

        return Either.right(Optional.of(result.getValue()));
    }

}
