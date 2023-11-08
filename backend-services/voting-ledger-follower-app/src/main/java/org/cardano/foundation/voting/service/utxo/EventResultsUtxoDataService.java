package org.cardano.foundation.voting.service.utxo;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.UtxoCategoryResultsData;
import org.cardano.foundation.voting.repository.UtxoCategoryResultsDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventResultsUtxoDataService {

    private final UtxoCategoryResultsDataRepository utxoCategoryResultsDataRepository;

    @Transactional
    @Timed(value = "service.utxo.store", histogram = true)
    public void storeUtxoData(UtxoCategoryResultsData utxoCategoryResultsData) {
        utxoCategoryResultsDataRepository.saveAndFlush(utxoCategoryResultsData);
    }

    @Transactional
    @Timed(value = "service.utxo.getLastValidResult", histogram = true)
    public Optional<UtxoCategoryResultsData> findLastValidResult(String contractAddress) {
        return utxoCategoryResultsDataRepository.findByAddress(contractAddress)
                .stream().max((utxoCategoryResultsData1, utxoCategoryResultsData2) -> {
                    if (utxoCategoryResultsData1.getAbsoluteSlot() == utxoCategoryResultsData2.getAbsoluteSlot()) {
                        return 0;
                    }

                    return (utxoCategoryResultsData1.getAbsoluteSlot() > utxoCategoryResultsData2.getAbsoluteSlot()) ? 1 : -1;
                });
    }

    @Transactional
    @Timed(value = "service.utxo.rollbackAfterSlot", histogram = true)
    public int rollbackAfterSlot(long slot) {
        log.info("Rollbacking UtxoData after slot:{}", slot);

        return utxoCategoryResultsDataRepository.deleteAllAfterSlot(slot);
    }

}
