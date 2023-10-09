package org.cardano.foundation.voting.shell;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraTransactionClient;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.utils.Partitioner;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellOption;
import shaded.com.google.common.collect.Lists;

import static java.util.concurrent.TimeUnit.MINUTES;
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
    private HydraTransactionClient hydraTransactionClient;

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
        return hydraTransactionClient.getHydraState().toString();
    }

    @Command(command = "connect", description = "connects to the hydra cluster.")
    public String connect() throws InterruptedException {
        hydraTransactionClient.openConnection(1, MINUTES);

        return "Connecting...";
    }

    @Command(command = "disconnect", description = "disconnect from the hydra cluster.")
    public String disconnect() throws InterruptedException {
        hydraTransactionClient.closeConnection();

        return "Disconnecting...";
    }

    @Command(command = "abort", description = "aborting from the hydra cluster.")
    public String abort() {
        var res = hydraTransactionClient.abortHead();

        if (res) {
            log.info("Aborting from the hydra network...");
        }

        return "Cannot abort, unsupported state, hydra state:" + hydraTransactionClient.getHydraState();
    }

    @Command(command = "init", description = "init.")
    public String init() throws InterruptedException {
        var res = hydraTransactionClient.initHead();

        if (res) {
            return "Init...";
        }

        return "Cannot init..., network stake:" + hydraTransactionClient.getHydraState();
    }

    @Command(command = "commit-empty", description = "commit no funds.")
    public String commitEmpty() {
        var res = hydraTransactionClient.commitEmptyToTheHead();

        if (res) {
            return "Committing no funds...";
        }

        return "Cannot commit, unsupported state, hydra state:" + hydraTransactionClient.getHydraState();
    }

    @Command(command = "commit-funds", description = "commit funds.")
    public String commitFunds() {
        var res = hydraTransactionClient.commitFundsToTheHead(cardanoCommitAddress, cardanoCommitUtxo, cardanoCommitAmount);

        if (res) {
            return "Committing utxo... " + cardanoCommitUtxo + " with amount: " + cardanoCommitAmount + " to address: " + cardanoCommitAddress;
        }

        return "Cannot commit, unsupported state, hydra state:" + hydraTransactionClient.getHydraState();
    }

    @Command(command = "fan-out", description = "fan out.")
    public String fanOut() {
        var res = hydraTransactionClient.fanOutHead();

        if (res) {
            return "Fan out...";
        }

        return "Cannot fan out, unsupported state, hydra state:" + hydraTransactionClient.getHydraState();
    }

    @Command(command = "close-head", description = "close head.")
    public String closeHead() {
        var res = hydraTransactionClient.closeHead();

        if (res) {
            return "Closing the head...";
        }

        return "Cannot close the head, unsupported state, hydra state:" + hydraTransactionClient.getHydraState();
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
