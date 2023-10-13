package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.UtxoSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.CategoryResultsDatum;
import org.cardano.foundation.voting.domain.UTxOCategoryResult;
import org.cardano.foundation.voting.domain.UTxOVote;
import org.cardano.foundation.voting.domain.VoteDatum;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;
import static org.cardano.foundation.voting.utils.MoreComparators.createCategoryResultTxHashAndTransactionIndexComparator;
import static org.cardano.foundation.voting.utils.MoreComparators.createVoteTxHashAndTransactionIndexComparator;
import static org.springframework.util.StringUtils.hasLength;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteUtxoFinder {

    private final UtxoSupplier utxoSupplier;
    private final PlutusScriptLoader plutusScriptLoader;

    public List<UTxOVote> getUtxosWithVotes(String eventId,
                                            String contractCategoryId,
                                            int batchSize) {
        val contract = plutusScriptLoader.getContract(eventId, contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        return utxoSupplier.getAll(contractAddress)
                .parallelStream()
                .filter(utxo -> hasLength(utxo.getInlineDatum()))
                .map(utxo -> {
                    val voteDatumE = VoteDatum.deserialize(decodeHexString(utxo.getInlineDatum()));

                    return new UTxOVote(utxo, voteDatumE.orElse(null));
                })
                .filter(uTxOVote -> uTxOVote.voteDatum() != null)
                .filter(uTxOVote -> uTxOVote.voteDatum().getCategoryId().equals(contractCategoryId))
                .sorted(createVoteTxHashAndTransactionIndexComparator())
                .limit(batchSize)
                .toList();
    }

    public List<UTxOCategoryResult> getUtxosWithCategoryResults(String eventId,
                                                                String contractCategoryId,
                                                                int batchSize) {
        val contract = plutusScriptLoader.getContract(eventId, contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        return utxoSupplier.getAll(contractAddress)
                .parallelStream()
                .filter(utxo -> hasLength(utxo.getInlineDatum()))
                .map(utxo -> {
                    val inlineDatum = utxo.getInlineDatum();
                    val inlineDatumHex = decodeHexString(inlineDatum);

                    val categoryResultsDatumM = CategoryResultsDatum.deserialize(inlineDatumHex);

                    return new UTxOCategoryResult(utxo, categoryResultsDatumM.fold(problem -> null, v -> v.orElse(null)));
                })
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum() != null)
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum().getCategoryId().equals(contractCategoryId))
                .sorted(createCategoryResultTxHashAndTransactionIndexComparator())
                .limit(batchSize)
                .toList();
    }

}
