package org.cardano.foundation.voting.service.auth.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.CardanoNetwork;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.domain.web3.SignedWeb3Request;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardanofoundation.cip30.Cip30VerificationResult;

import java.util.Map;

@Getter
@Builder
public class Web3Details {

    private String stakeAddress;
    private ChainFollowerClient.EventDetailsResponse event;
    private Web3Action action;
    private Cip30VerificationResult cip30VerificationResult;
    private CIP93Envelope<Map<String, Object>> envelope;
    private SignedWeb3Request signedWeb3Request;
    private ChainFollowerClient.ChainTipResponse chainTip;
    private CardanoNetwork network;

}
