package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Arrays;
import java.util.List;

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

    // blake2b 224 hashes of the verification keys of the witnesses
    @Column(name = "witnesses_hashes", nullable = false, columnDefinition = "text", length = 2048)
    private String witnessesHashes;

    public List<String> getWitnessesHashes() {
        return Arrays.asList(witnessesHashes.split(":"));
    }

}
