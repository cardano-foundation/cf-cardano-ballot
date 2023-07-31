package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "merkle_root_hash")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class MerkleRootHash extends AbstractTimestampEntity {

    @Id
    @Column(name = "merkle_root_hash", nullable = false)
    private String merkleRootHash;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "absolute_slot")
    private long absoluteSlot;

}
