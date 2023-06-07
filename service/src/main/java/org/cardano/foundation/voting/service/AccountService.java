package org.cardano.foundation.voting.service;

import org.cardano.foundation.voting.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private BlockchainDataService blockchainDataService;

    public Optional<Account> findAccount(String stakeAddress) {
        return blockchainDataService.getVotingPower(stakeAddress).map(votingPower -> Account.builder()
               .stakeAddress(stakeAddress)
               .votingPower(votingPower)
               .build());
    }

}
