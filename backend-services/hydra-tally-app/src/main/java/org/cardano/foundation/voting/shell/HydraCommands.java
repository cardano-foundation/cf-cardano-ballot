package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.client.util.JsonUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.CommitType;
import org.cardano.foundation.voting.domain.LocalBootstrap;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraVoteBatchReducer;
import org.cardano.foundation.voting.service.HydraVoteBatcher;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.service.PlutusScriptLoader;
import org.cardanofoundation.hydra.cardano.client.lib.submit.TransactionSubmissionService;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.Wallet;
import org.cardanofoundation.hydra.cardano.client.lib.wallet.WalletSupplier;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.model.http.HeadCommitResponse;
import org.cardanofoundation.hydra.core.model.query.response.*;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.cardanofoundation.hydra.reactor.HydraReactiveWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;
import reactor.core.Disposable;
import shaded.com.google.common.collect.Lists;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Map;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static io.netty.util.internal.StringUtil.isNullOrEmpty;
import static org.cardanofoundation.hydra.cardano.client.lib.utils.TransactionSigningUtil.sign;
import static org.cardanofoundation.hydra.core.model.HydraState.Initializing;
import static org.cardanofoundation.hydra.core.model.HydraState.Open;

@Slf4j
@Command(group = "hydra-tally-app")
public class HydraCommands {

    @Autowired
    private CardanoNetwork network;

    @Autowired
    private UTxOStore uTxOStore;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private HydraVoteImporter hydraVoteImporter;

    @Autowired
    private HydraVoteBatcher hydraVoteBatcher;

    @Autowired
    private HydraVoteBatchReducer hydraVoteBatchReducer;

    @Autowired
    private PlutusScriptLoader plutusScriptLoader;

    @Autowired
    private WalletSupplier walletSupplier;

    @Autowired
    private HydraReactiveClient hydraClient;

    @Autowired
    private HydraReactiveWebClient hydraReactiveWebClient;

    @Autowired
    @Qualifier("l1-transaction-submission-service")
    private TransactionSubmissionService l1TransactionSubmissionService;

    @Value("${hydra.ws.url}")
    private String hydraWsUrl;

    @Value("${hydra.operator.name}")
    private String actor;

    @Autowired
    private Environment environment;

    @Value("${hydra.auto.connect:false}")
    private boolean autoConnect;

    @Value("${cardano.commit.type}")
    private CommitType commitType;

    @Value("${local.bootstrap}")
    private LocalBootstrap localBootstrap;

    @Value("${ballot.event.id}")
    private String eventId;

    @Nullable private Disposable stateQuerySubscription;

    @Nullable private Disposable responsesSubscription;

    private Wallet l1Wallet;

    private boolean tallyAllExecuted = false;

    @PostConstruct
    public void init() throws Exception {
        this.l1Wallet = walletSupplier.getWallet();
        if (autoConnect) {
            connect();
//            initHead();
//            commitFunds();
        }
    }

    @Command(command = "get-head-state", description = "gets the current hydra state.")
    public String getHydraState() {
        return hydraClient.getHydraState().toString();
    }

    @Command(command = "get-utxos", description = "gets current hydra's head UTxOs.")
    public String getUtxOs(@ShellOption(value = "address") @Option(required = false) String address) {
        GetUTxOResponse getUTxOResponse = hydraClient.getUTxOs().block(Duration.ofMinutes(5));

        if (getUTxOResponse == null) {
            return "Cannot connect, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        val sb = new StringBuilder();
        sb.append("HeadId: ");
        sb.append(getUTxOResponse.getHeadId());
        sb.append("\n\n");

        var no = 0;
        var utxos = getUTxOResponse.getUtxo()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (isNullOrEmpty(address)) {
                        return true;
                    }

                    return entry.getValue().getAddress().equalsIgnoreCase(address);
                })
                .toList();

        for (val utxo: utxos) {
            sb.append(String.format("%d. %s: %s", ++no, utxo.getKey(), utxo.getValue()));
            if (utxo.getValue().getInlineDatum() != null && !utxo.getValue().getInlineDatum().asText().equals("null")) {
                sb.append(JsonUtil.getPrettyJson(utxo.getValue().getInlineDatum()));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Command(command = "connect", description = "connects to the hydra network.")
    public String connect() {
        log.info("Connecting to the hydra network:{}", hydraWsUrl);

        GreetingsResponse greetingsResponse = hydraClient.openConnection().block(Duration.ofMinutes(5));

        if (greetingsResponse == null) {
            return "Cannot connect, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        this.stateQuerySubscription = hydraClient.getHydraStatesStream().doOnNext(hydraState -> {
            System.out.printf("%n%s -> %s%n", hydraState.oldState(), hydraState.newState());
        }).subscribe();

        this.responsesSubscription = hydraClient.getHydraResponsesStream().doOnNext(response -> {
            if (response.isFailure()) {
                log.error("Hydra error response: {}", response.getTag());
            } else {
                if (response instanceof CommittedResponse cr) {
                    for (val utxo : cr.getUtxo().entrySet()) {
                        log.info("utxo: {}, value: {}", utxo.getKey(), utxo.getValue());
                    }
                }
            }
        }).subscribe();

        return "Connected.";
    }

    @Command(command = "disconnect", description = "disconnect from the hydra network.")
    public String disconnect() throws InterruptedException {
        log.info("Disconnecting from the hydra network: {}", hydraWsUrl);

        if (stateQuerySubscription != null) {
            stateQuerySubscription.dispose();
        }

        if (responsesSubscription != null) {
            responsesSubscription.dispose();
        }

        Boolean disconnected = hydraClient.closeConnection()
                .block(Duration.ofMinutes(5));

        if (disconnected == null) {
            return "Cannot disconnect, unsupported state, hydra state: " + hydraClient.getHydraState();
        }

        return "Disconnected.";
    }

    @Command(command = "abort-head", description = "aborting from the hydra network.")
    public String abort() {
        log.info("Aborting from the hydra network...");

        HeadIsAbortedResponse headIsAbortedResponse = hydraClient.abortHead().block(Duration.ofMinutes(5));
        if (headIsAbortedResponse == null) {
            return "Cannot abort, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Aborted.";
    }

    @Command(command = "init", description = "inits the hydra head.")
    public String headInit() {
        return initHead();
    }

    @Command(command = "head-init", description = "inits the hydra head.")
    public String initHead() {
        log.info("Init the head...");

        var headIsInitializingResponse = hydraClient.initHead().block(Duration.ofMinutes(5));

        if (headIsInitializingResponse == null) {
            return "Cannot init, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        var sb = new StringBuilder();
        sb.append("HeadId: " + headIsInitializingResponse.getHeadId());
        sb.append("\n\n");

        for (var party : headIsInitializingResponse.getParties()) {
            sb.append("Party: "  + party);
            sb.append("\n");
        }
        sb.append("\n");

        sb.append("Head is initialized.");

        return sb.toString();
    }

    @Command(command = "head-commit-funds", description = "head commit funds.")
    public String commitFunds() {
        if (hydraClient.getHydraState() != Initializing) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return switch (commitType) {
            case COMMIT_FUNDS -> {
                val cardanoCommitAddress = environment.getProperty("cardano.commit.address");
                val cardanoCommitUtxo = environment.getProperty("cardano.commit.utxo");
                val cardanoCommitAmount = environment.getProperty("cardano.commit.amount", Long.class);

                log.info("Committing funds to the head, " +
                        "address: {}," +
                        " utxo: {}," +
                        " amount: {}", cardanoCommitAddress, cardanoCommitUtxo, cardanoCommitAmount);

                val utxo = new UTXO();

                utxo.setAddress(cardanoCommitAddress);
                utxo.setValue(Map.of("lovelace", BigInteger.valueOf(cardanoCommitAmount.longValue())));

                var commitMap = Map.of(cardanoCommitUtxo, utxo);

                HeadCommitResponse committedResponse = hydraReactiveWebClient.commitRequest(commitMap)
                        .block(Duration.ofMinutes(5));

                if (committedResponse == null) {
                    yield "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
                }

                var transactionBytes = decodeHexString(committedResponse.getCborHex());

                byte[] signedTx = sign(transactionBytes, l1Wallet.getSecretKey());

                var txResult = l1TransactionSubmissionService.submitTransaction(HexUtil.encodeHexString(signedTx));

                if (!txResult.isSuccessful()) {
                    yield "Cannot commit, transaction submission failed, reason: " + txResult.getResponse();
                }

                yield "Committed funds, L1 transactionId: " + txResult.getValue();
            }
            case COMMIT_EMPTY -> {
                log.info("Committing empty to the head...");

                var commitMap = Map.<String, UTXO>of();

                HeadCommitResponse committedResponse = hydraReactiveWebClient.commitRequest(commitMap)
                        .block(Duration.ofMinutes(5));

                if (committedResponse == null) {
                    yield "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
                }

                var transactionBytes = decodeHexString(committedResponse.getCborHex());

                byte[] signedTx = sign(transactionBytes, l1Wallet.getSecretKey());

                var txResult = l1TransactionSubmissionService.submitTransaction(HexUtil.encodeHexString(signedTx));

                if (!txResult.isSuccessful()) {
                    yield "Cannot commit, transaction submission failed, reason: " + txResult.getResponse();
                }

                yield "Committed funds, L1 transactionId: " + txResult.getValue();
            }
        };
    }

    @Command(command = "head-fan-out", description = "head fan out.")
    public String fanOut() {
        var headIsFinalizedResponse = hydraClient.fanOutHead()
                .block(Duration.ofMinutes(5));

        if (headIsFinalizedResponse == null) {
            return "Cannot fan out, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        headIsFinalizedResponse.getUtxo().forEach((key, value) -> {
            log.info("utxo: {}, value: {}", key, value);
        });

        return "Fan out completed.";
    }

    @Command(command = "head-close", description = "close head.")
    public String closeHead() {
        HeadIsClosedResponse headIsClosedResponse = hydraClient.closeHead()
                .block(Duration.ofMinutes(5));

        if (headIsClosedResponse == null) {
            return "Cannot close the head, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Head is closed.";
    }

    @Command(command = "tally-all", description = "tally all the votes votes.")
    public String tallyAll(
            @ShellOption(value = "import-batch-size", defaultValue = "25") @Option int importBatchSize,
            @ShellOption(value = "create-batch-size", defaultValue = "10") @Option int createBatchSize,
            @ShellOption(value = "reduce-batch-size", defaultValue = "10") @Option int reduceBatchSize
    ) throws Exception {
        if (tallyAllExecuted) {
            return "Tallying votes already executed.";
        }

        log.info("Tally all votes, importBatchSize: {}, createBatchSize: {}, reduceBatchSize: {}",
                importBatchSize, createBatchSize, reduceBatchSize);

        if (hydraClient.getHydraState() != Open) {
            return "Tallying votes failed, reason:" + hydraClient.getHydraState();
        }

        var allCategories = voteRepository.getAllUniqueCategories(eventId);

        var organisers = plutusScriptLoader.getEventDetails().organisers();
        var votingEventType = plutusScriptLoader.getEventDetails().votingEventType();

        for (val categoryId : allCategories) {
            log.info("Processing category: {}", categoryId);

            var allVotes = voteRepository.findAllVotes(eventId, categoryId);
            var partitioned = Lists.partition(allVotes, importBatchSize);

            for (val voteBatch : partitioned) {
                val txIdE = hydraVoteImporter.importVotes(votingEventType, voteBatch);
                if (txIdE.isEmpty()) {
                    return "Importing votes failed, reason:" + txIdE.getLeft();
                }

                log.info("Imported votes voteBatch, txId: " + "{}", txIdE.get());

                hydraVoteBatcher.batchVotesPerCategory(eventId, organisers, categoryId, createBatchSize);
                hydraVoteBatchReducer.batchVotesPerCategory(eventId, organisers, categoryId, reduceBatchSize);
            }
        }

        this.tallyAllExecuted = true;

        return "Tallying votes done.";
    }

}
