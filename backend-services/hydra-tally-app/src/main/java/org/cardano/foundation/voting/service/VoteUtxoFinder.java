package org.cardano.foundation.voting.service;

import com.bloxbean.cardano.client.api.UtxoSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.cardano.foundation.voting.domain.UTxOCategoryResult;
import org.cardano.foundation.voting.domain.UTxOVote;
import org.cardano.foundation.voting.domain.converter.CategoryResultsDatumConverter;
import org.cardano.foundation.voting.domain.converter.VoteDatumConverter;
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
    private final VoteDatumConverter voteDatumConverter;
    private final CategoryResultsDatumConverter categoryResultsDatumConverter;

    public List<UTxOVote> getUtxosWithVotes(String contractEventId,
                                            String contractOrganiser,
                                            String contractCategoryId,
                                            int batchSize) {
        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        return utxoSupplier.getAll(contractAddress)
                .parallelStream()
                .filter(utxo -> hasLength(utxo.getInlineDatum()))
                .map(utxo -> {
                    try {
                        val voteDatum = voteDatumConverter.deserialize(utxo.getInlineDatum());

                        return new UTxOVote(utxo, voteDatum);
                    } catch (Exception e) {
                        return new UTxOVote(utxo, null);
                    }

                })
                .filter(uTxOVote -> uTxOVote.voteDatum() != null)
                .filter(uTxOVote -> uTxOVote.voteDatum().getEventId().equals(contractEventId))
                .filter(uTxOVote -> uTxOVote.voteDatum().getCategoryId().equals(contractCategoryId))
                .filter(uTxOVote -> uTxOVote.voteDatum().getOrganisers().equals(contractOrganiser))
                .sorted(createVoteTxHashAndTransactionIndexComparator())
                .limit(batchSize)
                .toList();
    }

    public List<UTxOCategoryResult> getUtxosWithCategoryResults(String eventId,
                                                                String organiser,
                                                                String contractCategoryId,
                                                                int batchSize) {
        val contract = plutusScriptLoader.getContract(contractCategoryId);
        val contractAddress = plutusScriptLoader.getContractAddress(contract);

        return utxoSupplier.getAll(contractAddress)
                .parallelStream()
                .filter(utxo -> hasLength(utxo.getInlineDatum()))
                .map(utxo -> {
                    val inlineDatum = utxo.getInlineDatum();
                    val inlineDatumHex = decodeHexString(inlineDatum);

                    try {
                        val categoryResultsDatum = categoryResultsDatumConverter.deserialize(inlineDatumHex);

                        return new UTxOCategoryResult(utxo, categoryResultsDatum);
                    } catch (Exception e) {
                        return new UTxOCategoryResult(utxo, null);
                    }
                })
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum() != null)
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum().getEventId().equals(eventId))
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum().getOrganiser().equals(organiser))
                .filter(uTxOCategoryResult -> uTxOCategoryResult.categoryResultsDatum().getCategoryId().equals(contractCategoryId))
                .sorted(createCategoryResultTxHashAndTransactionIndexComparator())
                .limit(batchSize)
                .toList();
    }

}
