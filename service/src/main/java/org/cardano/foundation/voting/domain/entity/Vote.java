package org.cardano.foundation.voting.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote")
@ToString
public class Vote {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "voter_stake_address")
    private String voterStakingAddress;

    @Column(name = "cose_signature")
    private String coseSignature;

    @Column(name = "cose_public_key")
    private String cosePublicKey;

    @Column(name = "voting_power")
    private long votingPower;

}
