package org.cardano.foundation.voting.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "vote_merkle_proof")
@ToString
public class VoteMerkleProof extends AbstractTimestampEntity {

    @Id
    @Column(name = "vote_id", nullable = false)
    private String voteId;

    @Column(name = "vote_id_numeric_hash", nullable = false)
    private long voteIdNumericHash;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "root_hash", nullable = false)
    private String rootHash;

    @Column(name = "l1_transaction_hash", nullable = false)
    private String l1TransactionHash;

    // TODO this could be more fancy and be in fact JSON / JSONB type: https://developer.okta.com/blog/2019/02/20/spring-boot-with-postgresql-flyway-jsonb
    @Column(name = "proof_items_json", nullable = false, columnDefinition = "text", length = 65535)
    private String proofItemsJson;

    @Column(name = "invalidated") // when there is a rollback event we soft delete it, invalidate the merkle proof
    private boolean invalidated;

    @Column(name = "absolute_slot", nullable = false)
    private Long absoluteSlot;

}
