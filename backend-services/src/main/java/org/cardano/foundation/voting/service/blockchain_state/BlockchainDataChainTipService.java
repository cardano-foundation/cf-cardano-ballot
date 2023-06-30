package org.cardano.foundation.voting.service.blockchain_state;


import org.cardano.foundation.voting.domain.ChainTip;

public interface BlockchainDataChainTipService {

    ChainTip getChainTip();

}
