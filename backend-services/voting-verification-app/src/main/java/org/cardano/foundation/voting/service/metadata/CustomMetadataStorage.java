package org.cardano.foundation.voting.service.metadata;

import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataLabel;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.MetadataMapper;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.TxMetadataStorageImpl;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.jpa.repository.TxMetadataLabelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class CustomMetadataStorage extends TxMetadataStorageImpl {

    @Value("${l1.transaction.metadata.label}")
    private long metadataLabel;

    public CustomMetadataStorage(TxMetadataLabelRepository metadataLabelRepository,
                                 MetadataMapper metadataMapper) {
        super(metadataLabelRepository, metadataMapper);
    }

    @Override
    public List<TxMetadataLabel> saveAll(List<TxMetadataLabel> txMetadataLabelList) {
        List<TxMetadataLabel> filteredMetadataLabels = txMetadataLabelList.stream()
                .filter(txMetadataLabel -> String.valueOf(metadataLabel).equals(txMetadataLabel.getLabel()))
                .toList();


        return super.saveAll(filteredMetadataLabels);
    }

}