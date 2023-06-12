package org.cardano.foundation.voting.service;



import org.cardano.foundation.voting.domain.Account;
import org.cardano.foundation.voting.domain.Network;
import org.cardano.foundation.voting.domain.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private BlockchainDataService blockchainDataService;

    public Optional<Account> findAccount(Network network, Event event, String stakeAddress) {
        return blockchainDataService.getVotingPower(network, event, stakeAddress).map(votingPower -> Account.builder()
               .stakeAddress(stakeAddress)
               .votingPower(votingPower)
               .build());
    }

}
