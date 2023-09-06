package org.cardano.foundation.voting.service.yaci;

import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.helper.BlockSync;
import com.bloxbean.cardano.yaci.helper.listener.BlockChainDataListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.ProtocolMagic;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class RollbackHandler {

    @Value("${cardano.node.ip}")
    private String cardanoNodeIp;

    @Value("${cardano.node.port}")
    private int cardanoNodePort;

    @Value("${rollback.handling.enabled}")
    private boolean isRollbackHandlingEnabled;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private ProtocolMagic protocolMagic;

    @Autowired
    private Point wellKnownPoint;

    private Optional<BlockSync> blockSync = Optional.empty();

    @PostConstruct
    public void init() {
        if (!isRollbackHandlingEnabled) {
            log.info("Rollback handler disabled.");
            return;
        }

        var networkMagic = protocolMagic.magic();
        var blockSync = new BlockSync(cardanoNodeIp, cardanoNodePort, networkMagic, wellKnownPoint);
        blockSync.startSyncFromTip(new BlockChainDataListener() {

            @Override
            public void onBlock(Block block) {
                log.info("Block's slot:{}, hash:{}, blockNo:{}", block.getHeader().getHeaderBody().getSlot(), block.getHeader().getHeaderBody().getBlockHash(), block.getHeader().getHeaderBody().getBlockNumber());
            }

            @Override
            public void onRollback(Point point) {
                var slot = point.getSlot();

                voteMerkleProofService.softDeleteAllProofsAfterSlot(slot);
            }

        });

        this.blockSync = Optional.of(blockSync);
    }

    @PreDestroy
    public void destroy() {
        log.info("Stopping block sync...");

        blockSync.ifPresent(BlockSync::stop);
    }

}
