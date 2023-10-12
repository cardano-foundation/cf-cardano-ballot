package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import com.bloxbean.cardano.client.plutus.annotation.PlutusField;
import com.bloxbean.cardano.client.plutus.spec.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Constr(alternative = 0)
@Data
@AllArgsConstructor
@Slf4j
public class CategoryResultsDatum {

    @PlutusField
    private String categoryId;

    @PlutusField
    private Map<String, Long> results;

    public static CategoryResultsDatum empty(String categoryId) {
        return new CategoryResultsDatum(categoryId, new HashMap<>());
    }

    public void add(String proposal, long result) {
        results.put(proposal, result);
    }

    public Long get(String proposal) {
        return results.get(proposal);
    }

    public Long getOr(String proposal, long defaultValue) {
        return results.getOrDefault(proposal, defaultValue);
    }

    public static Optional<CategoryResultsDatum> deserialize(byte[] datum) {
        try {
            val constr = (ConstrPlutusData) PlutusData.deserialize(datum);
            val list = constr.getData().getPlutusDataList();

            if (list.isEmpty()) {
                return Optional.empty();
            }

            val categoryIdPlutus = (BytesPlutusData) list.get(0);
            val resultsMapPlutus = (MapPlutusData) list.get(0);

            val entries = resultsMapPlutus.getMap().entrySet().iterator();

            val categoryId = new String(categoryIdPlutus.getValue(), UTF_8);

            val resultBatchDatum = CategoryResultsDatum.empty(categoryId);

            while (entries.hasNext()) {
                val entry = entries.next();

                val proposalPlutus = ((BytesPlutusData) entry.getKey()).getValue();
                val results = (BigIntPlutusData) entry.getValue();

                var proposal = new String(proposalPlutus);

                resultBatchDatum.add(proposal, results.getValue().longValue());
            }

            return Optional.of(resultBatchDatum);
        } catch (Exception e) {
            log.trace("Error in deserialization (VoteDatum)", e);
            return Optional.empty();
        }
    }
}
