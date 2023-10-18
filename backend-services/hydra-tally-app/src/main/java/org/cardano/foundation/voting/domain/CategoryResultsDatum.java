package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import com.bloxbean.cardano.client.plutus.annotation.Enc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;

@Constr
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResultsDatum {

    @Enc(value = "US_ASCII")
    private byte[] categoryId;

    private Map<byte[], Long> results;

    public static CategoryResultsDatum empty(byte[] categoryId) {
        return new CategoryResultsDatum(categoryId, new LinkedHashMap<>());
    }

    public void add(byte[] proposalId, long newResult) {
        val existingResult = results.getOrDefault(proposalId, 0L);

        results.put(proposalId, existingResult + newResult);
    }

}
