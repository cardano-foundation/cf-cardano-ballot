package org.cardano.foundation.voting.service.yaci;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataLabel;
import com.bloxbean.cardano.yaci.store.metadata.storage.TxMetadataStorage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class YaciCustomMetadataStorage implements TxMetadataStorage {


    @Override
    public List<TxMetadataLabel> saveAll(List<TxMetadataLabel> txMetadataLabelList) {
        return List.of();
    }

    @Override
    public List<TxMetadataLabel> findByTxHash(String txHash) {
        return List.of();
    }

    @Override
    public List<TxMetadataLabel> findByLabel(String label, int page, int count) {
        return List.of();
    }

    @Override
    public int deleteBySlotGreaterThan(long slot) {
        return 0;
    }

}

