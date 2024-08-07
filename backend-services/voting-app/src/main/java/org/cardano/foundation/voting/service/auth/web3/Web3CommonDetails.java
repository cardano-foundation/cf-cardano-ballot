package org.cardano.foundation.voting.service.auth.web3;

import lombok.Builder;
import lombok.Getter;
import org.cardano.foundation.voting.client.ChainFollowerClient;
import org.cardano.foundation.voting.domain.ChainNetwork;
import org.cardano.foundation.voting.domain.web3.CIP93Envelope;
import org.cardano.foundation.voting.domain.web3.SignedCIP30;
import org.cardano.foundation.voting.domain.web3.WalletType;
import org.cardano.foundation.voting.domain.web3.Web3Action;
import org.cardanofoundation.cip30.Cip30VerificationResult;

import java.util.Map;

@Getter
@Builder
public class Web3CommonDetails {

    private WalletType walletType;
    private String walletId;
    private ChainFollowerClient.EventDetailsResponse event;
    private Web3Action action;
    private ChainNetwork network;
    private ChainFollowerClient.ChainTipResponse chainTip;

}
