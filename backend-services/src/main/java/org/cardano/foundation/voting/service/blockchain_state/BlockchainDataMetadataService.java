package org.cardano.foundation.voting.service.blockchain_state;

import java.util.List;
import java.util.Map;

public interface BlockchainDataMetadataService {

    List<Map> fetchMetadataForLabel(String metadataLabel, int pageSize, int page);

}
