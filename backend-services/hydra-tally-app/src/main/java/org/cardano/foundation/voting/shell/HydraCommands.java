package org.cardano.foundation.voting.shell;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.Vote;
import org.cardano.foundation.voting.repository.VoteRepository;
import org.cardano.foundation.voting.service.HydraVoteImporter;
import org.cardano.foundation.voting.utils.Partitioner;
import org.cardanofoundation.hydra.client.HydraClientOptions;
import org.cardanofoundation.hydra.client.HydraWSClient;
import org.cardanofoundation.hydra.client.SLF4JHydraLogger;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.store.UTxOStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellOption;
import shaded.com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.Map;

import static org.cardano.foundation.voting.utils.MoreComparators.createVoteComparator;
import static org.cardanofoundation.hydra.core.model.HydraState.*;

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

    private HydraWSClient hydraClient;

    @PostConstruct
    public void init() {
        var options = HydraClientOptions.builder(hydraWsUrl)
                .withUTxOStore(uTxOStore)
                .history(false)
                .build();

        this.hydraClient = new HydraWSClient(options);

        var slf4JHydraLogger = new SLF4JHydraLogger(log, actor, false);

        //hydraClient.addHydraQueryEventListener(slf4JHydraLogger);
        hydraClient.addHydraStateEventListener(slf4JHydraLogger);
    }

    @Command(command = "connect", description = "connects to the hydra cluster.")
    public String connect() throws InterruptedException {
        hydraClient.connect();

        return "Connecting...";
    }

    @Command(command = "disconnect", description = "disconnect from the hydra cluster.")
    public String disconnect() throws InterruptedException {
        hydraClient.close();

        return "Disconnecting...";
    }

    @Command(command = "abort", description = "aborting from the hydra cluster.")
    public String abort() {
        if (hydraClient.getHydraState() == Initializing) {
            hydraClient.abort();

            return "Aborting...";
        }

        return "Cannot abort, unsupported state, hydra state:" + hydraClient.getHydraState();
    }

    @Command(command = "commit-empty", description = "commit no funds.")
    public String commitEmpty() {
        if (hydraClient.getHydraState() == Initializing) {
            hydraClient.commit();

            return "Committing no funds...";
        }

        return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
    }

    @Command(command = "commit-funds", description = "commit funds.")
    public String commitFunds() {
        if (hydraClient.getHydraState() == Initializing) {
            var u = new UTXO();

            u.setAddress(cardanoCommitAddress);
            u.setValue(Map.of("lovelace", BigInteger.valueOf(cardanoCommitAmount)));

            hydraClient.commit(cardanoCommitUtxo, u);

            return "Committing utxo... " + u;
        }

        return "Cannot commit, unsupported state, hydra state:" + hydraClient.getHydraState();
    }

    public String fanOut() {
        if (hydraClient.getHydraState() == FanoutPossible) {
            hydraClient.fanOut();

            return "Fan out...";
        }

        return "Cannot fan out, unsupported state, hydra state:" + hydraClient.getHydraState();
    }

    public String closeHead() {
        if (hydraClient.getHydraState() == Open) {
            log.info("Closing the head...");

            hydraClient.closeHead();

            return "Closing the head...";
        }

        return "Cannot close the head, unsupported state, hydra state:" + hydraClient.getHydraState();
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
