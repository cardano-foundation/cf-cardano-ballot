package org.cardano.foundation.voting.service.yaci;

import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.helper.BlockSync;
import com.bloxbean.cardano.yaci.helper.listener.BlockChainDataListener;
import com.bloxbean.cardano.yaci.helper.model.Transaction;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.WellKnownPointWithProtocolMagic;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class RollbackHandler {

    @Value("${cardano.node.ip}")
    private String cardanoNodeIp;

    @Value("${cardano.node.port}")
    private int cardanoNodePort;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private WellKnownPointWithProtocolMagic wellKnownPointWithProtocolMagic;

    private Optional<BlockSync> blockSync = Optional.empty();

    @PostConstruct
    public void init() {
        log.info("Starting cardano block sync on network: {}...", cardanoNetwork);

        if (wellKnownPointWithProtocolMagic.wellKnownPointForNetwork().isEmpty()) {
            log.warn("Well known point is not known. Skipping rollback handler / sync...");
            return;
        }

        var wellKnownPoint = wellKnownPointWithProtocolMagic.wellKnownPointForNetwork().orElseThrow();

        var protocolMagic = wellKnownPointWithProtocolMagic.protocolMagic();
        var blockSync = startBlockSync(protocolMagic, wellKnownPoint);

        this.blockSync = Optional.of(blockSync);
    }

    private BlockSync startBlockSync(long protocolMagic, Point wellKnownPoint) {
        var blockSync = new BlockSync(cardanoNodeIp, cardanoNodePort, protocolMagic, wellKnownPoint);
        blockSync.startSyncFromTip(new BlockChainDataListener() {

            @Override
            public void onBlock(Era era, Block block, List<Transaction> transactions) {
                var headerBody = block.getHeader().getHeaderBody();

                log.info("Block's slot:{}, hash:{}, blockNo:{}", headerBody.getSlot(), headerBody.getBlockHash(), headerBody.getBlockNumber());
            }

            @Override
            public void onRollback(Point point) {
                var slot = point.getSlot();

                voteMerkleProofService.softDeleteAllProofsAfterSlot(slot);
            }

        });

        return blockSync;
    }

    @PreDestroy
    public void destroy() {
        log.info("Stopping block sync...");

        blockSync.ifPresent(BlockSync::stop);
    }

}
