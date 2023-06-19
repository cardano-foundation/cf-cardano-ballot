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
@Table(name = "root_hash")
@Getter
@Setter
@ToString
public class RootHash extends AbstractTimestampEntity {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "root_hash")
    private String rootHash;

}
