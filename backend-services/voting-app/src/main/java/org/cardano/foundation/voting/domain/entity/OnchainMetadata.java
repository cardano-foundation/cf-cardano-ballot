package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.cardano.foundation.voting.domain.metadata.OnChainEventType;

@Getter
@Builder
@AllArgsConstructor
@Table(name = "onchain_metadata")
@Entity
@NoArgsConstructor
@Setter
public class OnchainMetadata {

    @Column
    @Id
    private String id;

    @Column(name = "onchain_event_type")
    private OnChainEventType onChainEventType;

    @Column(name = "metadata_label")
    private String metadataLabel;

    @Column(name = "address")
    private String address;

    @Column(name = "signature")
    private String signature;

    @Column(name = "key")
    private String key;

}
