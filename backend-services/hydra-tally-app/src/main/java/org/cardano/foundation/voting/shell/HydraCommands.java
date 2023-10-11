package org.cardano.foundation.voting.shell;

import io.netty.util.internal.StringUtil;
import io.vavr.control.Either;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.utils.Partitioner;
import org.cardanofoundation.hydra.core.model.HydraState;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.model.query.response.*;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellOption;
import org.zalando.problem.Problem;
import shaded.com.google.common.collect.Lists;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Map;

import static org.cardano.foundation.voting.utils.MoreComparators.createVoteComparator;

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

    @PostConstruct
    public void init() {
        if (autoConnect) {
            connect();
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

        StringBuilder sb = new StringBuilder();

        sb.append("HeadId: ");
        sb.append(getUTxOResponse.getHeadId());
        sb.append("\n\n");

        getUTxOResponse.getUtxo()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (StringUtil.isNullOrEmpty(address)) {
                        return true;
                    }

                    return entry.getValue().getAddress().equalsIgnoreCase(address);
                })
                .forEach(entry -> {

                    sb.append(String.format("%s: %s", entry.getKey(), entry.getValue()));
                    sb.append("\n");
                });

        return sb.toString();
    }

    @Command(command = "connect", description = "connects to the hydra network.")
    public String connect() {
        log.info("Connecting to the hydra network:{}", hydraWsUrl);

        GreetingsResponse greetingsResponse = hydraClient.openConnection().block(Duration.ofMinutes(1));

        hydraClient.getHydraStatesStream().doOnNext(hydraState -> {
            System.out.printf("%n%s -> %s%n", hydraState.getOldState(), hydraState.getNewState());
        }).subscribe();

        if (greetingsResponse == null) {
            return "Cannot connect, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Connected.";
    }

    @Command(command = "disconnect", description = "disconnect from the hydra network.")
    public String disconnect() throws InterruptedException {
        log.info("Disconnecting from the hydra network:{}", hydraWsUrl);

        Boolean disconnected = hydraClient.closeConnection().block(Duration.ofMinutes(1));

        if (disconnected == null) {
            return "Cannot disconnect, unsupported state, hydra state:" + hydraClient.getHydraState();
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

    @Command(command = "head-commit-empty", description = "commit no funds.")
    public String commitEmpty() {
        CommittedResponse committedResponse = hydraClient.commitEmptyToTheHead().block(Duration.ofMinutes(1));

        if (committedResponse == null) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        committedResponse.getUtxo().forEach((key, value) -> {
            log.info("utxo: {}, value: {}", key, value);
        });

        return "Committed empty (no funds).";
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

    @Command(command = "ready-fan-out", description = "ready to fan out.")
    public String readyFanOut() {
        var readyToFanoutResponse = hydraClient.readyToFanOut().block(Duration.ofMinutes(1));

        if (readyToFanoutResponse == null) {
            return "Cannot fan out, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

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

    @Command(command = "import-votes", description = "import votes.")
    public String importVotes(@ShellOption(value = "batch-size", defaultValue = "10") @Option(required = false) int batchSize) throws Exception {
        log.info("Import votes...");

        if (hydraClient.getHydraState() == HydraState.Open) {
            var participantVotes = StreamEx.of(voteRepository.findAllVotes(eventId))
                    .filter(v -> v.eventId().equals(eventId))
                    .filter(v -> {
                        int participant = Partitioner.partition(v.voteId(), participants);

                        return participant == participantNumber;
                    })
                    .sorted(createVoteComparator())
                    .distinct(Vote::voteId)
                    .toList();

            for (var vote : participantVotes) {
                log.info("Vote: {}", vote);
            }

            var partitioned = Lists.partition(participantVotes, batchSize);

            for (var batch : partitioned) {
                Either<Problem, String> txIdE = hydraVoteImporter.importVotes(batch);
                if (txIdE.isEmpty()) {
                    return "Importing votes failed, reason:" + txIdE.getLeft();
                }

                log.info("Imported votes batch, txId:{}", txIdE.get());
            }

            return "Imported votes.";
        }


        return "Cannot import votes, unsupported state, hydra state:" + hydraClient.getHydraState();
    }

    @Command(command = "batch-votes", description = "batch votes.")
    public String batchVotes() {
        log.info("Batch votes...");

        // look at number of votes
        // calculate tx count
        // get from to contract address and
        // send back to the contract address

        return "Batching votes...";
    }

    @Command(command = "reduce-votes", description = "reduce votes.")
    public String reduceVotes() {
        log.info("Reduce votes...");

        // TODO

        return "Reducing votes...";
    }

}
