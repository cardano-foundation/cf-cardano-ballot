package org.cardano.foundation.voting.shell;

import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.util.JsonUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraVoteBatchReducer;
import org.cardano.foundation.voting.service.HydraVoteBatcher;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.service.PlutusScriptLoader;
import org.cardano.foundation.voting.utils.Partitioner;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.model.query.response.*;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;
import reactor.core.Disposable;
import shaded.com.google.common.collect.Lists;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Map;

import static io.netty.util.internal.StringUtil.isNullOrEmpty;
import static org.cardano.foundation.voting.utils.MoreComparators.createVoteComparator;
import static org.cardanofoundation.hydra.core.model.HydraState.Open;

@Slf4j
@Command(group = "hydra")
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
    private HydraReactiveClient hydraClient;

    @Value("${hydra.ws.url}")
    private String hydraWsUrl;

    @Value("${hydra.operator.name}")
    private String actor;

    @Value("${cardano.commit.address}")
    private String cardanoCommitAddress;

    @Value("${cardano.commit.utxo}")
    private String cardanoCommitUtxo;

    @Value("${hydra.participants.count}")
    private int participants;

    @Value("${hydra.participant.number}")
    private int participantNumber;

    @Value("${cardano.commit.amount}")
    private Long cardanoCommitAmount;

    @Value("${ballot.event.id}")
    private String eventId;

    @Value("${hydra.auto.connect:false}")
    private boolean autoConnect;

    @Nullable private Disposable stateQuerySubscription;

    @Nullable private Disposable responsesSubscription;

    @PostConstruct
    public void init() {
        if (autoConnect) {
            connect();
            initHead();
            commitFunds();
        }
    }

    @Command(command = "get-head-state", description = "gets the current hydra state.")
    public String getHydraState() {
        return hydraClient.getHydraState().toString();
    }

    @Command(command = "get-utxos", description = "gets current hydra's head UTxOs.")
    public String getUtxOs(@ShellOption(value = "address") @Option(required = false) String address) {
        GetUTxOResponse getUTxOResponse = hydraClient.getUTxOs().block(Duration.ofMinutes(1));

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

        GreetingsResponse greetingsResponse = hydraClient.openConnection().block(Duration.ofMinutes(1));

        if (greetingsResponse == null) {
            return "Cannot connect, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        this.stateQuerySubscription = hydraClient.getHydraStatesStream().doOnNext(hydraState -> {
            System.out.printf("%n%s -> %s%n", hydraState.getOldState(), hydraState.getNewState());
        }).subscribe();

        this.responsesSubscription = hydraClient.getHydraResponsesStream().doOnNext(response -> {
            if (response.isFailure()) {
                log.error("Hydra error response: {}", response.getTag());
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

        Boolean disconnected = hydraClient.closeConnection().block(Duration.ofMinutes(1));

        if (disconnected == null) {
            return "Cannot disconnect, unsupported state, hydra state: " + hydraClient.getHydraState();
        }

        return "Disconnected.";
    }

    @Command(command = "abort-head", description = "aborting from the hydra network.")
    public String abort() {
        log.info("Aborting from the hydra network...");

        HeadIsAbortedResponse headIsAbortedResponse = hydraClient.abortHead().block(Duration.ofMinutes(1));
        if (headIsAbortedResponse == null) {
            return "Cannot abort, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Aborted.";
    }

    @Command(command = "head-init", description = "inits the hydra head.")
    public String initHead() {
        log.info("Init the head...");

        var headIsInitializingResponse = hydraClient.initHead().block(Duration.ofMinutes(1));

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
        val utxo = new UTXO();
        utxo.setAddress(cardanoCommitAddress);
        utxo.setValue(Map.of("lovelace", BigInteger.valueOf(cardanoCommitAmount)));

        var commitMap = Map.of(cardanoCommitUtxo, utxo);

        CommittedResponse committedResponse = hydraClient.commitFundsToTheHead(commitMap)
                .block(Duration.ofMinutes(1));

        if (committedResponse == null) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        committedResponse.getUtxo().forEach((key, value) -> {
            log.info("utxo: {}, value: {}", key, value);
        });

        return "Committed funds.";
    }

    @Command(command = "head-commit-my-funds", description = "head commit my funds.")
    public String commitFundsExplicitly(
            @ShellOption(value = "address") String address,
            @ShellOption(value = "utxo") String utxoString,
            @ShellOption(value = "amount") long amount
    ) {
        val utxo = new UTXO();
        utxo.setAddress(address);
        utxo.setValue(Map.of("lovelace", BigInteger.valueOf(amount)));

        var commitMap = Map.of(utxoString, utxo);

        CommittedResponse committedResponse = hydraClient.commitFundsToTheHead(commitMap)
                .block(Duration.ofMinutes(1));

        if (committedResponse == null) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        committedResponse.getUtxo().forEach((key, value) -> {
            log.info("utxo: {}, value: {}", key, value);
        });

        return "Committed funds.";
    }

    @Command(command = "head-fan-out", description = "head fan out.")
    public String fanOut() {
        var headIsFinalizedResponse = hydraClient.fanOutHead().block(Duration.ofMinutes(1));

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
        HeadIsClosedResponse headIsClosedResponse = hydraClient.closeHead().block(Duration.ofMinutes(1));

        if (headIsClosedResponse == null) {
            return "Cannot close the head, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Head is closed.";
    }

    @Command(command = "tally-all", description = "tally all the votes votes.")
    public String tallyAll(@ShellOption(value = "batch-size", defaultValue = "50") @Option int batchSize) throws Exception {
        log.info("Tally all votes, batchSize: {}", batchSize);

        if (hydraClient.getHydraState() != Open) {
            return "Tallying votes failed, reason:" + hydraClient.getHydraState();
        }

        var allCategories = voteRepository.getAllUniqueCategories(eventId);

        for (val categoryId : allCategories) {
            log.info("Processing category: {}", categoryId);

            var allVotes = voteRepository.findAllVotes(eventId, categoryId);
            var partitioned = Lists.partition(allVotes, batchSize);

            for (val voteBatch : partitioned) {
                val txIdE = hydraVoteImporter.importVotes(eventId, voteBatch);
                if (txIdE.isEmpty()) {
                    return "Importing votes failed, reason:" + txIdE.getLeft();
                }

                log.info("Imported votes voteBatch, txId: " + "{}", txIdE.get());

                hydraVoteBatcher.batchVotesPerCategory(eventId, categoryId, batchSize);
                hydraVoteBatchReducer.batchVotesPerCategory(eventId, categoryId, batchSize);
            }
        }

        return "Tallying votes done.";
    }

    @Command(command = "import-votes", description = "import votes.")
    public String importVotes(@ShellOption(value = "batch-size", defaultValue = "10") @Option int batchSize) throws Exception {
        log.info("Import votes, batchSize: {}", batchSize);

        if (hydraClient.getHydraState() != Open) {
            return "Importing votes failed, reason:" + hydraClient.getHydraState();
        }

        var participantVotes = StreamEx.of(voteRepository.findAllVotes(eventId))
                    .filter(v -> v.eventId().equals(eventId))
                    .filter(v -> {
                        int participant = Partitioner.partition(v.voteId(), participants);

                        return participant == participantNumber;
                    })
                    .sorted(createVoteComparator())
                    .distinct(Vote::voteId)
                    .toList();

        log.info("Total votes to import: {}", participantVotes.size());

        var partitioned = Lists.partition(participantVotes, batchSize);

        for (val batch : partitioned) {
            val txIdE = hydraVoteImporter.importVotes(eventId, batch);
            if (txIdE.isEmpty()) {
                return "Importing votes failed, reason:" + txIdE.getLeft();
            }

            log.info("Imported votes batch, txId: " + "{}", txIdE.get());
        }

        return "Imported votes.";
    }

    @Command(command = "batch-votes", description = "batch votes.")
    public String batchVotes(@ShellOption(value = "batch-size", defaultValue = "10") @Option int batchSize) throws Exception {
        log.info("Batch votes, batchSize: {}", batchSize);

        if (hydraClient.getHydraState() != Open) {
            return "Batching votes failed, reason:" + hydraClient.getHydraState();
        }

        val allCategories = voteRepository.getAllUniqueCategories(eventId);

        for (val categoryId : allCategories) {
            log.info("Processing category: {}", categoryId);
            hydraVoteBatcher.batchVotesPerCategory(eventId, categoryId, batchSize);
        }

        return "Vote batches creations done.";
    }

    @Command(command = "reduce-vote-results", description = "reduce vote results.")
    public String reduceVotes(@ShellOption(value = "batch-size", defaultValue = "10") @Option int batchSize) throws CborSerializationException, ApiException {
        log.info("Reduce vote results, batch size: {}", batchSize);

        if (hydraClient.getHydraState() != Open) {
            return "Batching votes failed, reason:" + hydraClient.getHydraState();
        }

        val allCategories = voteRepository.getAllUniqueCategories(eventId);

        for (val categoryId : allCategories) {
            log.info("Processing category: {}", categoryId);
            hydraVoteBatchReducer.batchVotesPerCategory(eventId, categoryId, batchSize);
        }

        return "Vote results reduced.";
    }

}
