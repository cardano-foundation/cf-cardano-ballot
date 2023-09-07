package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cardano.foundation.voting.domain.entity.AbstractTimestampEntity;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "merkle_root_hash")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Immutable
public class MerkleRootHash extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String merkleRootHash;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "absolute_slot", nullable = false)
    private long absoluteSlot;

    @Override
    public String toString() {
        return "MerkleRootHash{" +
                "merkleRootHash='" + merkleRootHash + '\'' +
                ", eventId='" + eventId + '\'' +
                ", absoluteSlot=" + absoluteSlot +
                ", createdAt=" + createdAt +
                '}';
    }

}
