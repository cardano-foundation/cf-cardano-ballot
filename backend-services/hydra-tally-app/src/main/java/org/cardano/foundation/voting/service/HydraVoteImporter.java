package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.ProtocolParamsSupplier;
import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.coinselection.impl.LargestFirstUtxoSelectionStrategy;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.function.TxBuilderContext;
import com.bloxbean.cardano.client.function.TxOutputBuilder;
import com.bloxbean.cardano.client.function.helper.BalanceTxBuilders;
import com.bloxbean.cardano.client.function.helper.InputBuilders;
import com.bloxbean.cardano.client.function.helper.MinAdaCheckers;
import com.bloxbean.cardano.client.transaction.spec.TransactionOutput;
import com.bloxbean.cardano.client.transaction.spec.Value;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.cardano.foundation.voting.domain.VotingEventType;
import org.cardano.foundation.voting.domain.converter.VoteDatumConverter;
import org.cardanofoundation.hydra.cardano.client.lib.submit.TransactionSubmissionService;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.WalletSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import java.util.List;

import static com.bloxbean.cardano.client.common.ADAConversionUtil.adaToLovelace;
import static java.math.BigDecimal.ZERO;

@Component
@Slf4j
public class HydraVoteImporter {

    @Autowired
    private UtxoSupplier utxoSupplier;

    @Autowired
    private ProtocolParamsSupplier protocolParamsSupplier;

    @Autowired
    @Qualifier("hydra-transaction-submission-service")
    private TransactionSubmissionService transactionSubmissionService;

    @Autowired
    private WalletSupplier walletSupplier;

    @Autowired
    private PlutusScriptLoader plutusScriptLoader;

    @Autowired
    private VoteDatumConverter voteDatumConverter;

    @Autowired
    private Network network;

    public Either<Problem, String> importVotes(VotingEventType votingEventType,
                                               List<Vote> votes) throws Exception {
        log.info("Importing number: {} votes", votes.size());

        val operator = walletSupplier.getWallet();
        val sender = operator.getBech32Address(network);

        if (votes.isEmpty()) {
            return Either.left(Problem.builder()
                    .withTitle("No votes to import")
                    .withDetail("No votes to import")
                    .build());
        }

        val voteDatumList = votes.stream()
                .map(vote -> {
                    val voteScore = switch (votingEventType) {
                        case USER_BASED:
                            yield vote.votingPower().orElse(1L);
                        case STAKE_BASED, BALANCE_BASED:
                            yield vote.votingPower().orElse(0L);
                    };

                    return VoteDatum.builder()
                                    .eventId(vote.eventId())
                                    .organisers(plutusScriptLoader.getEventDetails().organisers())
                                    .voteId(vote.voteId().toString())
                                    .voterKey(vote.voterStakeAddress())
                                    .categoryId(vote.categoryId())
                                    .proposalId(vote.proposalId().toString())
                                    .voteScore(voteScore)
                                    .build();
                        }
                ).toList();

        // Create an empty output builder
        TxOutputBuilder txOutputBuilder = (context, outputs) -> {};

        for (val voteDatum : voteDatumList) {
            val categoryId = voteDatum.getCategoryId();
            val contract = plutusScriptLoader.getContract(categoryId);
            val contractAddress = plutusScriptLoader.getContractAddress(contract);

            val datum = voteDatumConverter.toPlutusData(voteDatum);

//            System.out.println("Vote:" + JsonUtil.getPrettyJson(datum));

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

        val signedTx = operator.getTxSigner().sign(transaction);

        val result = transactionSubmissionService.submitTransaction(signedTx);
        if (!result.isSuccessful()) {
            return Either.left(Problem.builder()
                            .withTitle("Transaction submission failed")
                            .withDetail("Failure reason:" + result.getResponse())
                    .build());
        }

        return Either.right(result.getValue());
    }

}
