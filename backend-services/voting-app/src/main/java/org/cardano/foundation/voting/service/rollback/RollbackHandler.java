package org.cardano.foundation.voting.service.rollback;

import com.bloxbean.cardano.yaci.core.common.NetworkType;
import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.helper.BlockSync;
import com.bloxbean.cardano.yaci.helper.listener.BlockChainDataListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.service.merkle_tree.VoteMerkleProofService;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RollbackHandler {

    @Value("${l1.transaction.metadata.label:12345}")
    private long metadataLabel;

    @Value("${cardano.node.ip}")
    private String cardanoNodeIp;

    @Value("${cardano.node.port}")
    private int cardanoNodePort;

    @Autowired
    private CardanoNetwork cardanoNetwork;

    @Autowired
    private VoteMerkleProofService voteMerkleProofService;

    @Autowired
    private ReferenceDataService referenceDataService;

    @Autowired
    private Point wellKnownPointForNetwork;

    @Autowired
    private NetworkType networkType;

    @PostConstruct
    public void init() {
        BlockSync blockSync = new BlockSync(cardanoNodeIp, cardanoNodePort, networkType.getProtocolMagic(), wellKnownPointForNetwork);
        blockSync.startSyncFromTip(new BlockChainDataListener() {

            @Override
            public void onBlock(Block block) {
                log.info("Block's slot:{}, hash:{}, blockNo:{}", block.getHeader().getHeaderBody().getSlot(), block.getHeader().getHeaderBody().getBlockHash(), block.getHeader().getHeaderBody().getBlockNumber());
            }

            @Override
            public void onRollback(Point point) {
                var slot = point.getSlot();

                referenceDataService.rollbackReferenceDataAfterSlot(slot);
                voteMerkleProofService.softDeleteAllProofsAfterSlot(slot);
            }

        });
    }


}
