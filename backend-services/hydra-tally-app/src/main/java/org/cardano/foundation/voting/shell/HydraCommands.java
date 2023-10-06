package org.cardano.foundation.voting.shell;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardanofoundation.hydra.client.HydraClientOptions;
import org.cardanofoundation.hydra.client.HydraWSClient;
import org.cardanofoundation.hydra.client.SLF4JHydraLogger;
import org.cardanofoundation.hydra.core.model.UTXO;
import org.cardanofoundation.hydra.core.store.InMemoryUTxOStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.command.annotation.Command;

import java.math.BigInteger;
import java.util.Map;

import static org.cardanofoundation.hydra.core.model.HydraState.*;

@Slf4j
@Command(group = "hydra")
public class HydraCommands {

    @Autowired
    private CardanoNetwork network;

    @Value("${hydra.ws.url}")
    private String hydraWsUrl;

    @Value("${spring.profiles.active}")
    private String actor;

    @Value("${cardano.commit.address}")
    private String cardanoCommitAddress;

    @Value("${cardano.commit.utxo}")
    private String cardanoCommitUtxo;

    @Value("${cardano.commit.amount}")
    private Long cardanoCommitAmount;

    private HydraWSClient hydraClient;

    @PostConstruct
    public void init() {
        var options = HydraClientOptions.builder(hydraWsUrl)
                .withUTxOStore(new InMemoryUTxOStore())
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
    public String importVotes() {
        log.info("Import votes...");

        // connect to postgres db
        // get all the votes
        // batch them up
        // send then to contract address

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
