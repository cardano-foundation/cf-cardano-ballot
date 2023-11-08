package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "utxo_category_result")
@Getter
@Setter
public class UtxoCategoryResultsData {

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

    @Column(name = "witnesses", nullable = false, columnDefinition = "text", length = 1024)
    private String witnesses;

}
