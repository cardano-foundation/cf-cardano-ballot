//package org.cardano.foundation.voting.service.blockchain_state.yaci;
//
//import com.bloxbean.cardano.yaci.store.account.storage.AccountBalanceStorage;
//import com.bloxbean.cardano.yaci.store.utxo.service.UtxoService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.cardano.foundation.voting.service.blockchain_state.BlockchainDataStakePoolService;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class YaciStakePoolService implements BlockchainDataStakePoolService {
//
//    private AccountBalanceStorage accountBalanceStorage;
//
//    @Override
//    public Optional<Long> getStakeAmount(int epochNo, String stakeAddress) {
//        return accountBalanceStorage.getStakeAddressBalance(stakeAddress)
//                .stream()
//                .filter(balance -> balance.getEpoch() == epochNo)
//                .filter(balance -> balance.getUnit().equalsIgnoreCase("ADA"))
//                .map(stakeAddressBalance -> stakeAddressBalance.getQuantity().longValueExact())
//                .sorted(Collections.reverseOrder())
//                .findFirst();
//    }
//
//    @Override
//    public Optional<Long> getBalanceAmount(int epochNo, String stakeAddress) {
//        return Optional.empty();
//    }
//
//}
