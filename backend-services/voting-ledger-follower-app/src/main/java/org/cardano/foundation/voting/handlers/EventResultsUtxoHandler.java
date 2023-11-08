package org.cardano.foundation.voting.handlers;

import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.yaci.core.model.VkeyWitness;
import com.bloxbean.cardano.yaci.helper.model.Utxo;
import com.bloxbean.cardano.yaci.store.events.EventMetadata;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.entity.UtxoCategoryResultsData;
import org.cardano.foundation.voting.service.utxo.EventResultsUtxoDataService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventResultsUtxoHandler {

    private final CategoryResultsDatumConverter categoryResultsDatumConverter;

    private final EventResultsUtxoDataService eventResultsUtxoDataService;

    @EventListener
    @Async("singleThreadExecutor")
    public void processTransaction(TransactionEvent transactionEvent) {
        try {
            var txMetadata = transactionEvent.getMetadata();

            for (var trx : transactionEvent.getTransactions()) {
                for (var utxo : trx.getUtxos()) {
                    var address = utxo.getAddress();
                    var utxoCategoryDatumM = parseCategoryResultsDatum(utxo.getInlineDatum());

                    if (utxoCategoryDatumM.isEmpty()) {
                        continue;
                    }

                    var keys = trx.getWitnesses().getVkeyWitnesses()
                            .stream()
                            .map(VkeyWitness::getKey)
                            .toList();

                    log.info("witness keys:" + keys);

                    var keyHashes = trx.getWitnesses().getVkeyWitnesses()
                            .stream()
                            .map(VkeyWitness::getKey)
                            .map(this::getKeyHash)
                            .toList();

                    log.info("witness keys (hashes):" + keyHashes);

                    var utxoCategoryResultsData = prepareUtxoCategoryResults(utxo, address, keyHashes, txMetadata);

                    eventResultsUtxoDataService.storeUtxoData(utxoCategoryResultsData);
            };
        }
    } catch (Exception e) {
        log.warn("Error processing transaction event", e);
    }
}

    @Nullable private String getKeyHash(String pubKey) {
        try {
            return KeyGenUtil.getKeyHash(HexUtil.decodeHexString(pubKey));
        } catch (Exception e) {
            log.error("Error generating keyhash for key : " + pubKey, e);
        }
        return null;
    }

    private static UtxoCategoryResultsData prepareUtxoCategoryResults(Utxo utxo,
                                                                      String address,
                                                                      List<String> witnesses,
                                                                      EventMetadata txMetadata) {

        var joiner = new StringJoiner(":");
        witnesses.forEach(joiner::add);

        log.info("Preparing utxo category results data for utxo:{}", utxo);
        var utxoCategoryResultsData = new UtxoCategoryResultsData();
        utxoCategoryResultsData.setId(utxo.getTxHash() + "#" + utxo.getIndex());
        utxoCategoryResultsData.setAddress(address);
        utxoCategoryResultsData.setTxHash(utxo.getTxHash());
        utxoCategoryResultsData.setIndex(utxo.getIndex());
        utxoCategoryResultsData.setInlineDatum(utxo.getInlineDatum());
        utxoCategoryResultsData.setAbsoluteSlot(txMetadata.getSlot());
        utxoCategoryResultsData.setWitnesses(joiner.toString());

        return utxoCategoryResultsData;
    }

    private Optional<CategoryResultsDatum> parseCategoryResultsDatum(String inlineDatum) {
        try {
            return Optional.of(categoryResultsDatumConverter.deserialize(inlineDatum));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
