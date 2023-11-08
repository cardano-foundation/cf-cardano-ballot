package org.cardano.foundation.voting.handlers;

import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.yaci.core.model.BootstrapWitness;
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
import org.cardano.foundation.voting.service.plutus.PlutusScriptLoader;
import org.cardano.foundation.voting.service.reference_data.ReferenceDataService;
import org.cardano.foundation.voting.service.utxo.EventResultsUtxoDataService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.cardano.foundation.voting.domain.entity.Tally.TallyType.HYDRA;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventResultsUtxoHandler {

    private final PlutusScriptLoader plutusScriptLoader;

    private final ReferenceDataService referenceDataService;

    private final Network network;

    private final CategoryResultsDatumConverter categoryResultsDatumConverter;

    private final EventResultsUtxoDataService eventResultsUtxoDataService;

    @EventListener
    @Async("singleThreadExecutor")
    public void processTransaction(TransactionEvent transactionEvent) {
        try {
            var txMetadata = transactionEvent.getMetadata();

            var scriptAddressesWithWitnessesMap = prepareScriptAddressesWithWitnesses();

            for (var trx : transactionEvent.getTransactions()) {
                for (var utxo : trx.getUtxos()) {
                    var address = utxo.getAddress();

                    if (scriptAddressesWithWitnessesMap.containsKey(address)) {
                        var authorisedVerificationKeys = scriptAddressesWithWitnessesMap.get(address);

                        log.info("Found result contract address: {}, veriKeys:{}", address, authorisedVerificationKeys);

                        var utxoCategoryDatumM = parseCategoryResultsDatum(utxo.getInlineDatum());

                        if (utxoCategoryDatumM.isEmpty()) {
                            log.warn("Unable to process results for address:{}, utxo:{}, as datum is not parsable", address, utxo);
                            continue;
                        }

                        var utxoCategoryResultsData = prepareUtxoCategoryResults(utxo, address, txMetadata);
                        log.info("utxoCategoryResultsData:{}", utxoCategoryResultsData.getId());

                        var keys = trx.getWitnesses().getVkeyWitnesses()
                                .stream()
                                .map(VkeyWitness::getKey)
                                .map(this::getKeyHash)
                                .filter(Objects::nonNull)
                                .toList();
                        log.info("witness keys (hashes):" + keys);

                        var keys2 = trx.getWitnesses().getBootstrapWitnesses().stream()
                                .map(BootstrapWitness::getPublicKey)
                                .map(this::getKeyHash)
                                .filter(Objects::nonNull)
                                .toList();

                        log.info("bootstrap witness keys (hashes):" + keys2);

                        // check if verification key matches with our verification keys stored on chain

                        eventResultsUtxoDataService.storeUtxoData(utxoCategoryResultsData);
                    }
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

    private static UtxoCategoryResultsData prepareUtxoCategoryResults(Utxo utxo, String address, EventMetadata txMetadata) {
        log.info("Preparing utxo category results data for utxo:{}", utxo);
        var utxoCategoryResultsData = new UtxoCategoryResultsData();
        utxoCategoryResultsData.setId(utxo.getTxHash() + "#" + utxo.getIndex());
        utxoCategoryResultsData.setAddress(address);
        utxoCategoryResultsData.setTxHash(utxo.getTxHash());
        utxoCategoryResultsData.setIndex(utxo.getIndex());
        utxoCategoryResultsData.setInlineDatum(utxo.getInlineDatum());
        utxoCategoryResultsData.setAbsoluteSlot(txMetadata.getSlot());
        utxoCategoryResultsData.setDatumHash(Optional.ofNullable(utxo.getDatumHash()));

        return utxoCategoryResultsData;
    }

    private Map<String, List<String>> prepareScriptAddressesWithWitnesses() {
        var scriptAddresses = new HashMap<String, List<String>>();

        for (var event : referenceDataService.findAllValidEvents()) {
            for (var category : event.getCategories()) {
                for (var tally: event.getTallies()) {
                    if (tally.getType() == HYDRA) {
                        plutusScriptLoader.compileScriptBy(event, category.getId(), tally.getName()).ifPresent(plutusScript -> {
                            var address = plutusScriptLoader.getContractAddress(plutusScript, network);
                            var verificationKeysAsList = tally.getHydraTallyConfig().getVerificationKeysAsList();

                            scriptAddresses.put(address, verificationKeysAsList);
                        });
                    }
                }
            }
        }

        return scriptAddresses;
    }

    private Optional<CategoryResultsDatum> parseCategoryResultsDatum(String inlineDatum) {
        try {
            return Optional.of(categoryResultsDatumConverter.deserialize(inlineDatum));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
