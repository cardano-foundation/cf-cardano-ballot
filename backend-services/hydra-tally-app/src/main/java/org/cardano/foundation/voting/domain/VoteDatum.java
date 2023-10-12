package org.cardano.foundation.voting.domain;

import com.bloxbean.cardano.client.plutus.annotation.Constr;
import com.bloxbean.cardano.client.plutus.annotation.PlutusField;
import com.bloxbean.cardano.client.plutus.spec.BytesPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Constr(alternative = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class VoteDatum {

    @PlutusField
    private byte[] voterKey;

    @PlutusField
    private String categoryId;

    @PlutusField
    private String proposalId;

    public static Optional<VoteDatum> deserialize(byte[] datum) {
        try {
            PlutusData plutusData = PlutusData.deserialize(datum);
            if (!(plutusData instanceof ConstrPlutusData constr))
                return Optional.empty();

            if (constr.getData().getPlutusDataList().size() != 3) {
                return Optional.empty();
            }

            List<PlutusData> plutusDataList = constr.getData().getPlutusDataList();
            byte[] voterKey = ((BytesPlutusData) plutusDataList.get(0)).getValue();
            byte[] category = ((BytesPlutusData) plutusDataList.get(1)).getValue();
            byte[] proposal = ((BytesPlutusData) plutusDataList.get(2)).getValue();

            if (voterKey == null || category == null || proposal == null) {
                log.warn("Invalid VoteDatum. One of the fields is null. voterKey: {}, category: {}, proposal: {}",
                        voterKey, category, proposal);

                return Optional.empty();
            }

            return Optional.of(VoteDatum.builder()
                    .voterKey(voterKey)
                    .categoryId(new String(category))
                    .proposalId(new String(proposal))
                    .build()
            );
        } catch (Exception e) {
            log.trace("Error in deserialization (VoteDatum)", e);
            return Optional.empty();
        }
    }

}
