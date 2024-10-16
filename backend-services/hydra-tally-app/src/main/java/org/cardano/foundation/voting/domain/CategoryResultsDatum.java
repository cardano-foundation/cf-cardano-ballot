package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
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

    private String eventId;

    private String organiser;

    private String categoryId;

    private Map<String, Long> results;

    public static CategoryResultsDatum empty(String eventId, String organiser, String categoryId) {
        return new CategoryResultsDatum(eventId, organiser, categoryId, new LinkedHashMap<>());
    }

    public void add(String proposalId, long newResult) {
        val existingResult = results.getOrDefault(proposalId, 0L);

        results.put(proposalId, existingResult + newResult);
    }

}
