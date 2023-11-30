package org.cardano.foundation.voting.service.utxo;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.entity.EventResultsCategoryResultsUtxoData;
import org.cardano.foundation.voting.repository.UtxoCategoryResultsDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventResultsUtxoDataService {

    private final UtxoCategoryResultsDataRepository utxoCategoryResultsDataRepository;

    @Transactional
    @Timed(value = "service.results.store", histogram = true)
    public void storeEventResultsUtxoData(EventResultsCategoryResultsUtxoData eventResultsCategoryResultsUtxoData) {
        utxoCategoryResultsDataRepository.saveAndFlush(eventResultsCategoryResultsUtxoData);
    }

    @Transactional(readOnly = true)
    @Timed(value = "service.results.findAllResults", histogram = true)
    public List<EventResultsCategoryResultsUtxoData> findAllResults(String contractAddress) {
        return utxoCategoryResultsDataRepository.findByAddress(contractAddress)
                .stream()
                .sorted((eventResultsCategoryResultsUtxoData1, eventResultsCategoryResultsUtxoData2) -> {
                    if (eventResultsCategoryResultsUtxoData1.getAbsoluteSlot() == eventResultsCategoryResultsUtxoData2.getAbsoluteSlot()) {
                        return 0;
                    }

                    return (eventResultsCategoryResultsUtxoData1.getAbsoluteSlot() > eventResultsCategoryResultsUtxoData2.getAbsoluteSlot()) ? 1 : -1;
                })
                .toList();
    }

    @Transactional
    @Timed(value = "service.results.rollbackAfterSlot", histogram = true)
    public long rollbackAfterSlot(long slot) {
        log.info("Rollbacking EventResultsCategoryResultsUtxoData after slot:{}", slot);

        return utxoCategoryResultsDataRepository.deleteAllAfterSlot(slot);
    }

}
