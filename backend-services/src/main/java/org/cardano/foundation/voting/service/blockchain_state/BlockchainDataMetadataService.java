package org.cardano.foundation.voting.service.blockchain_state;

import java.util.List;

public interface BlockchainDataMetadataService {

    List<String> getAllMetadataForKey(String key);

}
