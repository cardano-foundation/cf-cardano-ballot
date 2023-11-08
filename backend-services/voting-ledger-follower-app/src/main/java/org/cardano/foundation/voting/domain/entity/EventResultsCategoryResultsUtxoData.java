package org.cardano.foundation.voting.domain.entity;

import com.bloxbean.cardano.client.util.HexUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Arrays;
import java.util.List;

import static com.bloxbean.cardano.client.crypto.KeyGenUtil.getKeyHash;
import static com.bloxbean.cardano.client.util.HexUtil.decodeHexString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "event_category_result_utxo_data")
@Getter
@Setter
public class EventResultsCategoryResultsUtxoData {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tx_hash", nullable = false)
    private String txHash;

    @Column(name = "index", nullable = false)
    private int index;

    @Column(name = "inline_datum", nullable = false, columnDefinition = "text", length = 2048)
    private String inlineDatum;

    @Column(name = "absolute_slot", nullable = false)
    private long absoluteSlot;

    @Column(name = "witnesses", nullable = false, columnDefinition = "text", length = 2048)
    private String witnesses;

    public List<String> getWitnessesAsList() {
        return Arrays.asList(witnesses.split(":"));
    }

    public List<String> getWitnessHashesesAsList() {
        return getWitnessesAsList()
                .stream()
                .map(witness -> getKeyHash(decodeHexString(witness)))
                .toList();
    }

}
