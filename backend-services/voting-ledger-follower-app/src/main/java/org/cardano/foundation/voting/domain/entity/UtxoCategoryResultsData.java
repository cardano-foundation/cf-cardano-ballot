package org.cardano.foundation.voting.domain.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "utxo_category_result")
public class UtxoCategoryResultsData {

    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private String id;

    @Column(name = "address", nullable = false)
    @Getter
    @Setter
    private String address;

    @Column(name = "tx_hash", nullable = false)
    @Getter
    @Setter
    private String txHash;

    @Column(name = "index", nullable = false)
    @Getter
    @Setter
    private int index;

    @Column(name = "inline_datum", nullable = false, columnDefinition = "text", length = 2048)
    @Getter
    @Setter
    private String inlineDatum;

    @Column(name = "datum_hash")
    @Nullable private String datumHash;

    @Column(name = "absolute_slot", nullable = false)
    @Getter
    @Setter
    private long absoluteSlot;

    public void setDatumHash(Optional<String> datumHash) {
        this.datumHash  = datumHash.orElse(null);
    }

    public Optional<String> getDatumHash() {
        return Optional.ofNullable(datumHash);
    }

}
