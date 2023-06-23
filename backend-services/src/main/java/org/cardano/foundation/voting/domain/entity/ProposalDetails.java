package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// We are not allowed to store any of this data on chain as this may have and in many cases will have PII data
@Entity
@Table(name = "proposal_details")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProposalDetails extends AbstractTimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "presentation_name", nullable = false)
    private String presentationName;

    // TODO i18n for presentationName

}
