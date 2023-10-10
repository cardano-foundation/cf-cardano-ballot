package org.cardano.foundation.voting.shell;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.utils.Partitioner;
import org.cardanofoundation.hydra.core.model.query.response.CommittedResponse;
import org.cardanofoundation.hydra.core.model.query.response.GreetingsResponse;
import org.cardanofoundation.hydra.core.model.query.response.HeadIsClosedResponse;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.cardanofoundation.hydra.reactor.HydraReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellOption;
import shaded.com.google.common.collect.Lists;

import java.time.Duration;

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

    @Command(command = "get-head-state", description = "gets the current hydra state.")
    public String getHydraState() {
        return hydraClient.getHydraState().toString();
    }

    @Command(command = "connect", description = "connects to the hydra network.")
    public String connect() throws InterruptedException {
        log.info("Connecting to the hydra network:{}", hydraWsUrl);

        GreetingsResponse greetingsResponse = hydraClient.openConnection().block(Duration.ofMinutes(1));

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

    @Command(command = "abort", description = "aborting from the hydra cluster.")
    public String abort() {
        log.info("Aborting from the hydra network...");

        hydraClient.abortHead().block(Duration.ofMinutes(1));

        return "Aborted.";
    }

    @Command(command = "init", description = "init.")
    public String init() throws InterruptedException {
        log.info("Init the head...");

        var headIsInitializingResponse = hydraClient.initHead().block(Duration.ofMinutes(1));

        if (headIsInitializingResponse == null) {
            return "Cannot init, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Head is initialized.";
    }

    @Command(command = "commit-empty", description = "commit no funds.")
    public String commitEmpty() {
        CommittedResponse committedResponse = hydraClient.commitEmptyToTheHead().block(Duration.ofMinutes(1));

        if (committedResponse == null) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Committed empty (no funds).";
    }

    @Command(command = "commit-funds", description = "commit funds.")
    public String commitFunds() {
        CommittedResponse committedResponse = hydraClient.commitEmptyToTheHead().block(Duration.ofMinutes(1));

        if (committedResponse == null) {
            return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Committed funds.";
    }

    @Command(command = "fan-out", description = "fan out.")
    public String fanOut() {
        var readyToFanoutResponse = hydraClient.fanOutHead().block(Duration.ofMinutes(1));

        if (readyToFanoutResponse == null) {
            return "Cannot fan out, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

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

    @Command(command = "close-head", description = "close head.")
    public String closeHead() {
        HeadIsClosedResponse headIsClosedResponse = hydraClient.closeHead().block(Duration.ofMinutes(1));

        if (headIsClosedResponse == null) {
            return "Cannot close the head, unsupported state, hydra state:" + hydraClient.getHydraState();
        }

        return "Head is closed.";
    }

    @Command(command = "import-votes", description = "import votes.")
    public String importVotes(@ShellOption(value = "batch-size", defaultValue = "10") int batchSize) throws Exception {
        log.info("Import votes...");

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
            var txId = hydraVoteImporter.importVotes(batch);

            log.info("Imported votes, txId:{}", txId);
        }

        return "Importing votes...";
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
