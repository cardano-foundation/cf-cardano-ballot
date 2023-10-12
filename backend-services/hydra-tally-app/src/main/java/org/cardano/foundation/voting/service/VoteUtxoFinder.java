package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.UtxoSupplier;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.util.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.bloxbean.cardano.client.api.common.OrderEnum.asc;
import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.utils.MoreComparators.createTxHashAndTransactionIndexComparator;
import static org.springframework.util.StringUtils.hasLength;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteUtxoFinder {

    private final UtxoSupplier utxoSupplier;
    private final PlutusScriptLoader plutusScriptLoader;

    public List<Tuple<Utxo, VoteDatum>> getUtxosWithVotes(String contractCategoryId,
                                                          int batchSize) {
        boolean isContinue = true;

        List<Tuple<Utxo, VoteDatum>> utxos = new ArrayList<>();
        int page = 0;

        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        while (isContinue) {
            var utxoList = utxoSupplier.getPage(contractAddress, batchSize, page++, asc);

            if (utxoList.isEmpty()) {
                isContinue = false;
                continue;
            }

            var utxoTuples = utxoList.stream()
                    .filter(utxo -> hasLength(utxo.getInlineDatum()))
                    .map(utxo -> {
                        val voteDatumE = VoteDatum.deserialize(decodeHexString(utxo.getInlineDatum()));

                        return new Tuple<>(utxo, voteDatumE.orElse(null));
                    })
                    .filter(utxoOptionalTuple -> utxoOptionalTuple._2 != null)
                    .filter(utxoOptionalTuple -> utxoOptionalTuple._2.getCategory().equals(contractCategoryId))
                    .sorted(createTxHashAndTransactionIndexComparator())
                    .toList();

            utxos.addAll(utxoTuples);

            if (utxos.size() >= batchSize) {
                utxos = utxos.subList(0, batchSize);
                isContinue = false;
            }

        }

        return utxos;
    }

    public List<Tuple<Utxo, CategoryResultsDatum>> getUtxosWithVoteBatches(String contractCategoryId, int batchSize) {
        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        var isContinue = true;

        List<Tuple<Utxo, CategoryResultsDatum>> utxos = new ArrayList<>();

        int page = 0;
        while (isContinue) {
            var utxoList = utxoSupplier.getPage(contractAddress, batchSize, page++, asc);
            if (utxoList.isEmpty()) {
                isContinue = false;
                continue;
            }

            val utxoTuples = utxoList.stream()
                    .filter(utxo -> hasLength(utxo.getInlineDatum()))
                    .map(utxo -> {
                        val inlineDatum = utxo.getInlineDatum();
                        val inlineDatumHex = decodeHexString(inlineDatum);

                        val categoryResultsDatumM = CategoryResultsDatum.deserialize(inlineDatumHex);

                        return new Tuple<>(utxo, categoryResultsDatumM.fold(problem -> null, v -> v.orElse(null)));
                    })
                    .filter(utxoOptionalTuple -> utxoOptionalTuple._2 != null)
                    .filter(utxoOptionalTuple -> utxoOptionalTuple._2.getCategoryId().equals(contractCategoryId))
                    .sorted(createTxHashAndTransactionIndexComparator())
                    .toList();

            utxos.addAll(utxoTuples);

            if (utxos.size() >= batchSize) {
                utxos = utxos.subList(0, batchSize);
                isContinue = false;
            }
        }

        return utxos;
    }

}
